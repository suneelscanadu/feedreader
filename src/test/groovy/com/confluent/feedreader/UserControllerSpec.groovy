package com.confluent.feedreader

import grails.converters.JSON
import grails.test.mixin.TestFor
import org.grails.web.converters.configuration.ConvertersConfigurationInitializer
import org.grails.web.converters.configuration.DefaultConverterConfiguration
import org.grails.web.converters.marshaller.json.ValidationErrorsMarshaller
import spock.lang.Specification

import grails.test.mixin.TestFor
import spock.lang.Specification
import grails.test.mixin.Mock

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(UserController)
@Mock([BootStrapDataService])
class UserControllerSpec extends Specification {

    def setup() {
		DefaultConverterConfiguration<JSON> defaultConverterConfig = new  DefaultConverterConfiguration<JSON>()
		def convertersInit = new ConvertersConfigurationInitializer()
		def jsonErrorMarshaller = new ValidationErrorsMarshaller()
		JSON.registerObjectMarshaller(jsonErrorMarshaller)
		
    }

    def cleanup() {
    }

    void "test user feed subscription"() {
		given:
		def userService = new UserService()
		controller.userService = userService
		
		def bootStrapDataService = Mock(BootStrapDataService)
		controller.bootStrapDataService = bootStrapDataService
		
		
		controller.request.method = 'POST'
		controller.request.contentType = 'application/json'
		
		when:
			request.json = '{"userId": 1, "feedId": 2}'
	   		controller.subscribeFeed()
	
		then:
		   response.status==400
    	}
    
}
