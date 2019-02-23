# mongo-testing-java8

Java 8 temporal classes (LocalDate, Instant etc.) do not seem to work in queries for grails 3/4 in tests.

A workaround exists for Grails 3 (with the java-8 plugin to manually configure the codecs:

```
import groovy.util.logging.Slf4j
import org.grails.plugins.bson.CodecRegistry
import org.spockframework.runtime.extension.AbstractGlobalExtension
import org.spockframework.runtime.model.SpecInfo

/**
 * grails-java8 codecs aren't registered during tests, so explicitly add them
 */
@Slf4j
class Java8CodecExtension extends AbstractGlobalExtension {
    @Override
    void visitSpec(SpecInfo specInfo) {

        specInfo.addSetupSpecInterceptor({ invocation ->

            log.info("Applying Java8 Codecs")
            CodecRegistry.registerDecoders()
            CodecRegistry.registerEncoders()

            invocation.proceed()
        })
    }
}
```

However, the java-8 plugin has been removed for Grails 4, and this workaround is no longer valid.

This project was created using Grails 4.0.0.M1

`create-app -features mongodb`
