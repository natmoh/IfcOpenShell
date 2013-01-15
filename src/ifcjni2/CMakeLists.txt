IF("$ENV{JAVA_HOME}" STREQUAL "")         
       MESSAGE(STATUS "Java Not found")               
ELSE()
	SET(JAVA_INCLUDE_DIR $ENV{JAVA_HOME}/include CACHE FILEPATH "Java header files")
	SET(JAVA_INCLUDE_LINUX_DIR $ENV{JAVA_HOME}/include/linux CACHE FILEPATH "Java header files for linux")
	MESSAGE(STATUS "Looking for jni header files in ${JAVA_INCLUDE_DIR} & ${JAVA_INCLUDE_LINUX_DIR}") 
ENDIF()

INCLUDE_DIRECTORIES(${JAVA_INCLUDE_DIR} ${JAVA_INCLUDE_LINUX_DIR})

ADD_LIBRARY(ifcjni2 SHARED 
  ../../src/ifcjni2/jni/library.cpp
)

TARGET_LINK_LIBRARIES (ifcjni2 IfcParse IfcGeom TKernel TKMath TKBRep TKGeomBase TKGeomAlgo TKG3d TKG2d TKShHealing TKTopAlgo TKMesh TKPrim TKBool TKBO TKFillet)
