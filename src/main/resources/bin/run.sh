#!/bin/bash

USER_NAME=jace
TARGET_NAME=pwdw/media_upload_test
PACKAGE_NAME=media_upload_test-0.0.1-SNAPSHOT
MAIN_CLASS_NAME=MediaUploadTestApplication
SERVICE_HOME=/home/${USER_NAME}/${TARGET_NAME}

PATH_TO_JAR=$SERVICE_HOME/lib/${PACKAGE_NAME}.jar
#JAVA_OPT="$JAVA_OPT -XX:+UseG1GC -XX:G1RSetUpdatingPauseTimePercent=5 -XX:MaxGCPauseMillis=500 -XX:+UseLargePages -verbosegc -Xms4G -Xmx4G -verbose:gc -Xlog:gc=debug:file=$SERVICE_HOME/logs/gc.log:time,uptime,level,tags:filecount=5,filesize=100m"
JAVA_OPT="$JAVA_OPT -Dspring.profiles.active=server"
JAVA_OPT="$JAVA_OPT -Dspring.config.location=${SERVICE_HOME}/config/application.yml"

function exec_start() {
        PID=`ps -ef | grep java | grep ${MAIN_CLASS_NAME} | awk '{print $2}'`
        if ! [ -z "$PID" ]
        then
                echo "[${PACKAGE_NAME}] is already running"
        else
                #ulimit -n 65535
                #ulimit -s 65535
                #ulimit -u 10240
                #ulimit -Hn 65535
                #ulimit -Hs 65535
                #ulimit -Hu 10240

                java -jar $JAVA_OPT $PATH_TO_JAR ${MAIN_CLASS_NAME} > /dev/null 2>&1 &
                echo "[${PACKAGE_NAME}] started ..."
        fi
}

function exec_stop() {
        PID=`ps -ef | grep java | grep ${MAIN_CLASS_NAME} | awk '{print $2}'`
        if [ -z "$PID" ]
        then
                echo "[${PACKAGE_NAME}] is not running"
        else
                echo "stopping [${PACKAGE_NAME}]"
                kill "$PID"
                sleep 1
                PID=`ps -ef | grep java | grep ${MAIN_CLASS_NAME} | awk '{print $2}'`
                if [ ! -z "$PID" ]
                then
                        echo "kill -9 ${PID}"
                        kill -9 "$PID"
                fi
                echo "[${PACKAGE_NAME}] stopped"
        fi
}

function exec_status() {
  PID=`ps -ef | grep java | grep ${MAIN_CLASS_NAME} | awk '{print $2}'`
        if [ -z "$PID" ]
        then
                echo "[${PACKAGE_NAME}] is not running"
        else
                echo "[${PACKAGE_NAME}] is running"
          ps -aux | grep ${MAIN_CLASS_NAME} | grep "$PID"
        fi
}

case $1 in
    restart)
                exec_stop
                exec_start
                ;;
    start)
                exec_start
    ;;
    stop)
                exec_stop
    ;;
    status)
    exec_status
    ;;
esac