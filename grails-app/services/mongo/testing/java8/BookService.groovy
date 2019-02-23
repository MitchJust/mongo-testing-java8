package mongo.testing.java8

import com.mongodb.client.model.Filters
import grails.gorm.transactions.Transactional

import java.time.Instant

@Transactional
class BookService {

    Book getBookWithPublished(Instant from) {
        Book.find(Filters.eq('published', from))
                .first()

    }

    Book getBookWithAuthor(String author) {
        Book.find(Filters.eq('author', author))
                .first()

    }
}
