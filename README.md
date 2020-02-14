# AutoDeployWar
It detects change in War file in every 0.5 sec and autmatically deploys the War file to the Wildfly server via CLI command.

## Configuratin

Change the configuration in file **"src/main/resources/config.txt"** as follows, there should be no extra space.

	WILDFLY_CLI=path/to/your/jboss-cli.sh
	WAR=path/to/your/war/file

## To Run This Tool

Enter following cmd in you CLI:

	mvn clean compile exec:java
