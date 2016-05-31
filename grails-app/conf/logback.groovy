import grails.util.BuildSettings
import grails.util.Environment


// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%level %logger - %msg%n"
    }
}

appender("FILE", FileAppender) {
	file = "logs/feedreader_log.log"
	append = true
	rollingFile name:"stacktrace",
	maxFileSize:'100KB'
	encoder(PatternLayoutEncoder) {
	  pattern = "%d{HH:mm:ss.SSS} %level %logger - %msg%n"
	  
	}
  }

root(ERROR, ['STDOUT', 'FILE'])

if(Environment.current == Environment.DEVELOPMENT) {
    def targetDir = BuildSettings.TARGET_DIR
    if(targetDir) {

        appender("FULL_STACKTRACE", FileAppender) {

            file = "${targetDir}/stacktrace.log"
            append = true
            encoder(PatternLayoutEncoder) {
                pattern = "%level %logger - %msg%n"
            }
        }
        logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false )
    }
}
