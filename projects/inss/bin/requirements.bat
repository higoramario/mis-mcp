@ECHO OFF

SET CP="%INSS_HOME%\libs\commons.jar"
SET CP="%INSS_HOME%\libs\inss.jar"
SET CP="%INSS_HOME%\libs\opal.jar"
SET CP=%CP%;"%INSS_HOME%\libs\commons-cli-1.2.jar"
SET CP=%CP%;"%INSS_HOME%\libs\log4j-1.2.16.jar"
SET CP=%CP%;"%INSS_HOME%\libs\gj-csv-1.0.jar"
SET CP=%CP%;"%INSS_HOME%\libs\dom4j-1.6.1.jar"
java -cp %CP% br.usp.each.inss.RequirementReader %*