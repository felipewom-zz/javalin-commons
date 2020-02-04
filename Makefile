deploy:
	@[ "${version}" ] && echo "Setting version to ${version}" && mvn org.codehaus.mojo:versions-maven-plugin:2.4:set -DnewVersion="${version}" || ( echo "VERSION NOT SET"; )
	mvn clean dokka:javadocJar package deploy
	git add .
	git commit -m "Changed version to ${version}"
	export BRANCH="$(git branch | grep \* | cut -d ' ' -f2)"
	git push origin HEAD:"${BRANCH}"
	git tag -a "${version}" -m "Version created and deployed ${version}"
	git push origin "${version}"
