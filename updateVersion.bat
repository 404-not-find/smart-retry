@REM 请先修改根目录下的pom.xml的版本号，然后执行本脚本批量把子模块的版本号一同更新
mvn -N -DprocessAllModules -DgenerateBackupPoms=false versions:update-child-modules