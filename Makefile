deploy:
	@[ "${version}" ] && echo "Setting version to ${version}" && mvn org.codehaus.mojo:versions-maven-plugin:2.4:set -DnewVersion="${version}" || ( echo "VERSION NOT SET"; )
	mvn clean dokka:javadocJar package deploy
	VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
	git add .
	git commit -m "Changed version to ${VERSION}"
	git push origin HEAD:$(git branch | grep \* | cut -d ' ' -f2)
	git tag -a ${VERSION} -m "Version created and deployed ${VERSION}"
	git push origin ${VERSION}
