The purpose of this tool is to detect Java files that contain unit test methods, find the
class under test for each of those files, and generate a CSV file suitable for usage with
tsDetect.

# Installation:
'''
mvn package
'''


# Usage:
'''
java -jar target/tsDetectFrontend-0.1-jar-with-dependencies.jar <Project name> <Root directory of project's source code> <Name for output CSV file>
'''

