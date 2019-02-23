package mongo.testing.java8

import com.mongodb.MongoClient
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Filters
import grails.test.mongodb.MongoSpec
import grails.testing.services.ServiceUnitTest
import org.bson.Document
import org.testcontainers.containers.GenericContainer

import java.time.Instant

class BookServiceSpec extends MongoSpec implements ServiceUnitTest<BookService> {

    @Override
    MongoClient createMongoClient() {
        // Start a mongo container and create a MongoClient connected to it
        GenericContainer mongo = new GenericContainer('mongo:3.6').withExposedPorts(27017)
        mongo.start()
        return new MongoClient(mongo.containerIpAddress, mongo.getMappedPort(27017))
    }

    def cleanup() {
        // Drop the database between tests
        getMongoClient().dropDatabase('test')
    }

    void "test search by Instant (FAIL)"() {
        given: "3 books"
        Book book1 = new Book(title: "Book 1", author: "An Author", published: Instant.ofEpochMilli(1000)).save(flush: true)
        Book book2 = new Book(title: "Book 2", author: "An Author", published: Instant.ofEpochMilli(2000)).save(flush: true)
        Book book3 = new Book(title: "Book 3", author: "An Author", published: Instant.ofEpochMilli(3000)).save(flush: true)

        expect: "Search using the bson find"
        service.getBookWithPublished(Instant.ofEpochMilli(2000)) == book2
    }

    void "test search by GORM (FAIL)"() {
        given:
        Book book1 = new Book(title: "Book 1", author: "An Author", published: Instant.ofEpochMilli(1000)).save(flush: true)
        Book book2 = new Book(title: "Book 2", author: "An Author", published: Instant.ofEpochMilli(2000)).save(flush: true)
        Book book3 = new Book(title: "Book 3", author: "An Author", published: Instant.ofEpochMilli(3000)).save(flush: true)

        expect: "Search using GORM"
        Book.findByPublished(Instant.ofEpochMilli(2000)) == book2
     }

    void "test search with no params (PASS)"() {
        Book book1 = new Book(title: "Book 1", author: "An Author", published: Instant.ofEpochMilli(1000)).save(flush: true)
        Book book2 = new Book(title: "Book 2", author: "An Author", published: Instant.ofEpochMilli(2000)).save(flush: true)
        Book book3 = new Book(title: "Book 3", author: "An Author", published: Instant.ofEpochMilli(3000)).save(flush: true)

        expect: "Manually load all Books and find match in memory"
        Book.all.find{it.published == Instant.ofEpochMilli(2000)} == book2
    }

    void "test search by String (PASS)"() {
        given:
        Book book1 = new Book(title: "Book 1", author: "An Author 1", published: Instant.ofEpochMilli(1000)).save(flush: true)
        Book book2 = new Book(title: "Book 2", author: "An Author 2", published: Instant.ofEpochMilli(2000)).save(flush: true)
        Book book3 = new Book(title: "Book 3", author: "An Author 3", published: Instant.ofEpochMilli(3000)).save(flush: true)

        expect: "Search using the bson find"
        service.getBookWithAuthor("An Author 3") == book3
    }

    void "test search by Aggregate Pipeline (String) (PASS)"() {
        given:
        Book book1 = new Book(title: "Book 1", author: "An Author 1", published: Instant.ofEpochMilli(1000)).save(flush: true)
        Book book2 = new Book(title: "Book 2", author: "An Author 2", published: Instant.ofEpochMilli(2000)).save(flush: true)
        Book book3 = new Book(title: "Book 3", author: "An Author 3", published: Instant.ofEpochMilli(3000)).save(flush: true)

        when:
        List<Document> books = Book.collection.aggregate([
                Aggregates.match(Filters.eq('title', "Book 1"))
        ]).collect()

        then:
        books.size() == 1
    }

    void "test search by Aggregate Pipeline (Instant) (FAIL)"() {
        given:
        Book book1 = new Book(title: "Book 1", author: "An Author 1", published: Instant.ofEpochMilli(1000)).save(flush: true)
        Book book2 = new Book(title: "Book 2", author: "An Author 2", published: Instant.ofEpochMilli(2000)).save(flush: true)
        Book book3 = new Book(title: "Book 3", author: "An Author 3", published: Instant.ofEpochMilli(3000)).save(flush: true)

        when:
        List<Document> books = Book.collection.aggregate([
                Aggregates.match(Filters.eq('published', Instant.ofEpochMilli(3000)))
        ]).collect()

        then:
        books.size() == 1
    }
}
