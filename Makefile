deploy:
	git add .
	git commit -m "Changed version to ${version}"
	export BRANCH="$(git branch | grep \* | cut -d ' ' -f2)"
	git push origin HEAD:"${BRANCH}"
	git tag -a "${version}" -m "Version created and deployed ${version}"
	git push origin "${version}"
