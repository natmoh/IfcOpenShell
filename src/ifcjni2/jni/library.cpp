/********************************************************************************
*                                                                              *
* This file is part of IfcOpenShell.                                           *
*                                                                              *
* IfcOpenShell is free software: you can redistribute it and/or modify         *
* it under the terms of the Lesser GNU General Public License as published by  *
* the Free Software Foundation, either version 3.0 of the License, or          *
* (at your option) any later version.                                          *
*                                                                              *
* IfcOpenShell is distributed in the hope that it will be useful,              *
* but WITHOUT ANY WARRANTY; without even the implied warranty of               *
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the                 *
* Lesser GNU General Public License for more details.                          *
*                                                                              *
* You should have received a copy of the Lesser GNU General Public License     *
* along with this program. If not, see <http://www.gnu.org/licenses/>.         *
*                                                                              *
********************************************************************************/

/********************************************************************************
*                                                                              *
* This is a JNI interface to IfcOpenShell 		                               *
*                                                                              *
********************************************************************************/

#include "org_ifcopenshell_jni_Library.h"
#include "../../ifcgeom/IfcGeomObjects.h"
#include <iostream>

bool has_more = false;
using namespace std;

JNIEXPORT jobject JNICALL Java_org_ifcopenshell_jni_Library_getIfcGeometry (JNIEnv * env, jobject jobj) {
	if ( ! has_more ) return 0;
	

	jclass geom_class_def = env->FindClass ("org/ifcopenshell/model/IfcGeometryObject");
	if ( ! geom_class_def ) return 0;

	jclass mesh_class_def = env->FindClass ("org/ifcopenshell/model/IfcMesh");
	if ( ! mesh_class_def ) return 0;

	jmethodID jconstructor_geom = env->GetMethodID(geom_class_def, "<init>", "(IILorg/ifcopenshell/model/IfcMesh;[FLjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
	
	if ( ! jconstructor_geom ) return 0;

	jmethodID jconstructor_mesh = env->GetMethodID(mesh_class_def, "<init>", "([I[F)V");
	if ( ! jconstructor_mesh ) return 0;

	const IfcGeomObjects::IfcGeomObject* o = IfcGeomObjects::Get();

	jstring name = env->NewStringUTF(o->name.c_str());
	jstring type = env->NewStringUTF(o->type.c_str());
	jstring guid = env->NewStringUTF(o->guid.c_str());

	jintArray indices = env->NewIntArray(o->mesh->faces.size());
	jfloatArray positions = env->NewFloatArray(o->mesh->verts.size());
//	jfloatArray normals = env->NewFloatArray(o->mesh->normals.size());
	jfloatArray matrix = env->NewFloatArray(o->matrix.size());

	env->SetIntArrayRegion(indices,0,o->mesh->faces.size(),(jint*) &o->mesh->faces[0]);
	env->SetFloatArrayRegion(positions,0,o->mesh->verts.size(),(jfloat*) &o->mesh->verts[0]);
//	env->SetFloatArrayRegion(normals,0,o->mesh->normals.size(),(jfloat*) &o->mesh->normals[0]);
	env->SetFloatArrayRegion(matrix,0,o->matrix.size(),(jfloat*) &o->matrix[0]);

        jobject mesh_obj = env->NewObject(mesh_class_def, jconstructor_mesh, indices, positions);

	jobject geom_obj = env->NewObject(geom_class_def, jconstructor_geom, o->id, o->parent_id, mesh_obj, matrix, type, guid, name);

	env->DeleteLocalRef(name);
	env->DeleteLocalRef(type);
	env->DeleteLocalRef(guid);
	env->DeleteLocalRef(indices);
	env->DeleteLocalRef(positions);
//	env->DeleteLocalRef(normals);
	env->DeleteLocalRef(matrix);
	env->DeleteLocalRef(mesh_class_def);
	env->DeleteLocalRef(geom_class_def);

	has_more = IfcGeomObjects::Next();
	//if ( ! has_more ) IfcGeomObjects::CleanUp();

	return geom_obj;
}

JNIEXPORT jboolean JNICALL Java_org_ifcopenshell_jni_Library_setIfcData (JNIEnv * env, jobject jobj, jbyteArray jdata) {
	if ( ! jdata ) return false;
	const int length = env->GetArrayLength(jdata);
	jboolean is_copy = false;
	jbyte* jbytes = env->GetByteArrayElements(jdata, &is_copy);
	if ( ! jbytes || ! length ) return false;
	void* data = new char[length];
	memcpy(data,jbytes,length);
	env->ReleaseByteArrayElements(jdata, jbytes, JNI_ABORT);
	IfcGeomObjects::Settings(IfcGeomObjects::USE_WORLD_COORDS, false);
	IfcGeomObjects::Settings(IfcGeomObjects::WELD_VERTICES, false);
	IfcGeomObjects::Settings(IfcGeomObjects::CONVERT_BACK_UNITS, true);
	return has_more = IfcGeomObjects::Init(data, length);
}


JNIEXPORT jboolean JNICALL Java_org_ifcopenshell_jni_Library_setIfcPath (JNIEnv * env, jobject jobj, jstring fpath){
  
	const char *s = env->GetStringUTFChars(fpath, 0);
        cout << string(s);
        if(s == NULL) return false;
//        const std::string filepath(s);
	IfcGeomObjects::Settings(IfcGeomObjects::CONVERT_BACK_UNITS, true);
	has_more = IfcGeomObjects::Init(string(s), 0, 0);
	env->ReleaseStringUTFChars(fpath, s);	
	return has_more;
   
}

/*
 * Class:     org_ifcopenshell_jni_Library
 * Method:    getObject
 * Signature: (I)Lorg/ifcopenshell/model/IfcObject;
 */
JNIEXPORT jobject JNICALL Java_org_ifcopenshell_jni_Library_getObject (JNIEnv * env, jobject jobj, jint oid){

jclass geom_class_def = env->FindClass ("org/ifcopenshell/model/IfcObject");
	if ( ! geom_class_def ) return 0;

	jmethodID jconstructor_geom = env->GetMethodID(geom_class_def, "<init>", "(IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;[F)V");
	
	if ( ! jconstructor_geom ) return 0;

	const IfcGeomObjects::IfcObject* o = IfcGeomObjects::GetObject(oid);
    if(o == NULL) return 0;

	jstring name = env->NewStringUTF(o->name.c_str());
	jstring type = env->NewStringUTF(o->type.c_str());
	jstring guid = env->NewStringUTF(o->guid.c_str());

	jfloatArray matrix = env->NewFloatArray(o->matrix.size());

	env->SetFloatArrayRegion(matrix,0,o->matrix.size(),(jfloat*) &o->matrix[0]);

	jobject geom_obj = env->NewObject(geom_class_def, jconstructor_geom, o->id, o->parent_id, type, guid, name, matrix);

	env->DeleteLocalRef(name);
	env->DeleteLocalRef(type);
	env->DeleteLocalRef(guid);
	env->DeleteLocalRef(matrix);
	env->DeleteLocalRef(geom_class_def);

	return geom_obj;
    
}


/*
 * Class:     org_ifcopenshell_jni_Library
 * Method:    Cleanup
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_ifcopenshell_jni_Library_Cleanup (JNIEnv * env, jobject jobj){
   IfcGeomObjects::CleanUp();
}



