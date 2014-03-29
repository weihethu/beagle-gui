beagle-gui
=====================
##What is it
**[Beagle](http://sts.thss.tsinghua.edu.cn/beagle)** is a verification tool for combinational transition systems (CTS).
It checks whether the system satisfies the specified properties.

**beagle-gui** is the desktop IDE for beagle. It's written in JAVA and can run on Windows or Linux.

**beagle-gui** has the following features:

* create/open/save projects
* edit models in GUI
* connect with beagle backend to verify models on specific properties

The following is a snapshot of **beagle-gui** when editing models:

![model editing](https://raw.githubusercontent.com/weihethu/beagle-gui/master/snapshots/module_edit.png "model editing")

The following is a snapshot of **beagle-gui** when verifing models:

![verifing](https://raw.githubusercontent.com/weihethu/beagle-gui/master/snapshots/verify_properties.png "verifying")

##How to run it

* clone this repository
* place `deployment\beagle-gui.jar` & `beagle_gui.config` & `beagles` in the same directory
* `java -jar beagle-gui.jar`

##How to compile it

* clone this repository
* import as existing projects in [Eclipse](http://www.eclipse.org)

##About author
Wei He, a master student at Tsinghua University.