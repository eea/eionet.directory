####################################################################################
# makes RPM source package and install package
# of JAR
####################################################################################
# usage makerpm.sh [version]
# if version specified (1.0), the tag has to be created in CVS, called release_1_0
# if version is not specified,then 1.0, then the latestcode is taken from CVS
# and version no 1.0 is used as default

###################################################################################
# !!!!!!!!!! NEEDS ADJUSTEMENT !!!!!!!!!!!
# RPM directory : parent for SOURCE, BUILD and other rpm directories 
###################################################################################

# rpm_dir=/bin/rpm
rpm_dir=/usr/src/redhat

###################################################################################


ver=$1

if [ "$ver" = "" ] ; then
 ver="1.0"
 cvs export -D today -d eionet-dir-$ver Java/EionetDir
else
 rel=`echo $ver | tr "\." "_"`
 cvs export -r release_$rel -d eionet-dir-$ver Java/EionetDir
fi;

#############################################
# Export ANT libraries, needed for compiling
#############################################
cvs export -D today -d eionet-dir-$ver Java/lib

###########
# make tgz
###########
tar cfz eionet-dir.tgz eionet-dir-$ver

###############################
# copy *.spec file to working
###############################
cp eionet-dir-$ver/build/eionetdir.spec .

######################
# Remove temp folder
######################
rm -rf eionet-dir-$ver

######################
# RPM source directory
######################
if [ -d $rpm_dir/SOURCES ]; then
	mv eionet-dir.tgz $rpm_dir/SOURCES
else
	echo "RPM directory $rpm_dir does not exist. Cannot create RPM package"
	exit
fi;


############
#build rpm
############

rpm -ba eionetdir.spec

echo "Success!"

echo "TAR archive was created in $rpm_dir/SOURCES"
echo "The source package was created in $rpm_dir/SRPMS"
echo "The binary package can be find in $rpm_dir/RPMS/i386"
# cp $rpm_dir/SRPMS/*.rpm .
# cp $rpm_dir/RPMS/i386/*.rpm .

rm eionetdir.spec

##################
# README file
##################

echo "To install the binary package in default location type: rpm -i eionet-dir-$ver-1.i386.rpm" > EIONETDIR-README
echo "To install the binary package in a different location (e.g. /webs/webrod/WEB-INF/lib) type: rpm -i --prefix /webs/webrod/WEB-INF eionet-dir-$ver-1.i386.rpm" >> EIONETDIR-README

echo "To compile the source package type: rpm --rebuild eionet-dir-$ver-1.src.rpm" >> EIONETDIR-README
echo "Compiling requires rpm-build module to be installed. Type:  rpm -q rpm-build to check " >> EIONETDIR-README



