 Running IPhone Analyzer
 =======================
 
 
 Contributing to IPhone Analyzer
 ===============================
 
 
 ssh-keygen -t dsa
 copy and paste ~/.ssh/*_dsa.pub
 https://sourceforge.net/apps/trac/sourceforge/wiki/SSH%20keys
https://sourceforge.net/account/ssh

~/.m2/settings.xml

<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

<servers>
    <server>
      <id>ssh-repository</id>
      <username>leocrawford</username>
   </server>
</servers>

</settings>