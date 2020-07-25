To build all the modules run in the project root directory the following command with Maven 3:

	mvn clean install

Deploy AEM Application

	mvn clean install -Pdeploy-aem-app

To deploy it to a publish instance, run

	mvn clean install -P{profile-name} -Daem.server={protocol://host:port} -Daem.user={username} -Daem.password={password}