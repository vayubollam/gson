#!/usr/bin/env groovy

@NonCPS
def addRMPLinks(commitMessages) {

	//noinspection GroovyAssignabilityCheck
	lines = commitMessages.split("\n")

	lineBuilder = ""

	for (String line : lines) {

		lineBuilder += "\u2022 "
		lineBuilder += line
		lineBuilder += "\n"

		def matcher = line =~ /RMP-[0-9]+/
		while (matcher.find()) {
			String foundJIRANumber = matcher.group()
			lineBuilder += "\tlink: https://suncoragilecoe.atlassian.net/browse/" + foundJIRANumber + "\n"
		}
	}
	return lineBuilder
}

// Do NOT use NonCPS here!
def addBuildInfo(mfpTagName) {
	def now = new Date()
	def formattedNow = now.format("yyyy-MM-dd HH:mm:ss z", TimeZone.getTimeZone("EST5EDT"))

	def shortCommitHash = env.GIT_COMMIT.substring(0, 6)

	sh "sed -i '' 's/{JENKINS_INSERT_BUILD_TIME}/${formattedNow}/g' $CONFIG_MANAGER_PATH"
	sh "sed -i '' 's/{JENKINS_INSERT_TAG}/${mfpTagName}/g' $CONFIG_MANAGER_PATH"
	sh "sed -i '' 's/{JENKINS_INSERT_HASH}/${shortCommitHash}/g' $CONFIG_MANAGER_PATH"
	sh "sed -i '' 's/{JENKINS_INSERT_BUILD_NUMBER}/${env.BUILD_NUMBER}/g' $CONFIG_MANAGER_PATH"
	sh "sed -i '' 's/{JENKINS_INSERT_ENVIRONMENT}/${env.BUILD_ENV}/g' $CONFIG_MANAGER_PATH"
}

@NonCPS
def getSlackChannel(environment) {

	def SLACK_CHANNEL_DEV = "CFRDD6XLG"
	def SLACK_CHANNEL_QA = "GFG4ELAJF"
	def SLACK_CHANNEL_UAT = "CQT00VABF"
	def SLACK_CHANNEL_PROD = "CG4FLFNP3"
	def SLACK_CHANNEL_SUSTAINMENT_DEV = "CQV630ER4"
	def SLACK_CHANNEL_SUSTAINMENT_QA = "CQT041Y5B"

	def slackChannel = "";
	switch (environment) {
		case 'Dev':
			slackChannel = SLACK_CHANNEL_DEV
			break;
		case 'QA':
			slackChannel = SLACK_CHANNEL_QA
			break;
		case 'Prod':
			slackChannel = SLACK_CHANNEL_PROD
			break;
		case 'DevSus':
			slackChannel = SLACK_CHANNEL_SUSTAINMENT_DEV
			break;
		case 'QASus':
			slackChannel = SLACK_CHANNEL_SUSTAINMENT_QA
			break;
		case 'UAT':
			slackChannel = SLACK_CHANNEL_UAT
			break;
		default:
			echo "Cannot resolve build environment ${env.BUILD_ENV}"
			break;
	}

	return slackChannel;
}

@NonCPS
def getFormattedTime() {
	def now = new Date()
	return now.format("EEE MMM d, yyyy HH:mm:ss z", TimeZone.getTimeZone("EST5EDT"))
}

@NonCPS
def getBuildDate() {
	def now = new Date()
	return now.format("YYYYMMddHH", TimeZone.getTimeZone("EST5EDT"))
}


// Secret sauce to allow import into Jenkinsfile:
return this
