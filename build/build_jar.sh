######################################
# Builds JAR file
# uses relative paths
# !!!must be run in /build folder!!!
######################################


####################################
# Quick fix 
# Set libdir, depending on
# if we are in RPM build or not
####################################
# If we run from RPM libir is 

if [ -d ../../lib ]; then
	libdir=../../lib
else
	libdir=../
fi;

if [ ! -f build.xml ] ; then
	echo "This script must be run in EionetDir/build folder, Sorry"
	exit
fi;

buildfile=build.xml


if [ ! -d ../classes ]; then
	mkdir ../classes
fi;


libs=$libdir/ant.jar:$libdir/xml-apis.jar:$JAVA_HOME/lib/tools.jar

java -cp $libs org.apache.tools.ant.Main -f $buildfile


rm -fr ../classes

