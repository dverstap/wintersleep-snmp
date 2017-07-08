#@IgnoreInspection BashAddShebang
if [ -z ${TOOLS_DIR} ]
then
    echo "env var TOOLS_DIR not set"
    return
fi

JAVA_HOME=${TOOLS_DIR}/jdk7
export JAVA_HOME

PATH=${JAVA_HOME}/bin:$PATH
export PATH
