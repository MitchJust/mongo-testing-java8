package mongo.testing.java8

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import java.time.Instant

@EqualsAndHashCode
@ToString(includeNames = true)
class Book {

    String title
    String author
    Instant published

    static constraints = {
    }
}
