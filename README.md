To build all the modules run in the project root directory the following command with Maven 3:

	mvn clean install

If you have a running AEM instance you can build and package the whole project and deploy into AEM with

	mvn clean install -Pdeploy-aem

Or to deploy it to a publish instance, run

	mvn clean install -Pdeploy-aem -DslingServer={protocol://host:port} -Daem.username={username} -Daem.password={password}
