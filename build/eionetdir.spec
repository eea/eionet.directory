Summary: Eionet Directory
Name: eionet-dir
Version: 1.0
Release: 1
Source0: %{name}.tgz
License: GNU
Group: Small Java coponents
BuildRoot: %{_topdir}/tmp_root
Prefix: /opt/eionet/java
%description
Common and reusable component for EIONET directory operations
%prep
%setup -q
%build
 cd build
 chmod +x build_jar.sh
./build_jar.sh
%install
if [ ! -d %{buildroot}%{prefix}/lib ]; then
   mkdirhier %{buildroot}%{prefix}/lib
fi;
cp lib/eionet-dir.jar %{buildroot}%{prefix}/lib
cp etc/eionetdir.properties %{buildroot}%{prefix}
%clean
rm -r $RPM_BUILD_ROOT
cd ..
rm -r eionet-dir-%{version}
%files
%{prefix}/lib/eionet-dir.jar
%config %{prefix}/eionetdir.properties
