/*****************************************************************************
 *                        Shapeways, Inc Copyright (c) 2015
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 * This software comes with the standard NO WARRANTY disclaimer for any
 * purpose. Use it at your own risk. If there's a problem you get to fix it.
 *
 ****************************************************************************/

package abfab3d.param;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.vecmath.Vector3d;
import javax.vecmath.AxisAngle4d;

import abfab3d.core.Location;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import abfab3d.core.Color;

import static abfab3d.core.Output.printf;
import static abfab3d.core.Output.fmt;

/**
 * Utilities to read/write to from Json Maps
 *
 * @author Vladimir Bulatov
 */
public class ParamJson {
    
    static final String CLASS = "class";
    static final String PARAMS = "params";

    static final boolean DEBUG = false;

    /**
       convert array of values into form suitable for Json serialization
     */
    public static Object getJsonFromList(ArrayList list, ParameterType type){
        ArrayList vv = new ArrayList();
        if(list != null){
            for(Object item: list){
                vv.add(getJsonValue(item, type));        
            }
        }
        return vv;                
    }
    
    public static Object getJsonFromVector3d(Vector3d v){

        ArrayList vv = new ArrayList(3);
        if(v != null){
            vv.add(new Double(v.x));
            vv.add(new Double(v.y));
            vv.add(new Double(v.z));
        }
        return vv;
    }

    public static Object getJsonFromAxisAngle4d(AxisAngle4d a){
        ArrayList vv = new ArrayList(4);
        if(a != null){
            vv.add(new Double(a.x));
            vv.add(new Double(a.y));
            vv.add(new Double(a.z));
            vv.add(new Double(a.angle));
        }
        return vv;        
    }

    public static Object getJsonFromColor(Color c){
        ArrayList vv = new ArrayList(3);
        if(c != null){
            vv.add(new Double(c.getr()));
            vv.add(new Double(c.getg()));
            vv.add(new Double(c.getb()));
        }
        return vv;
    }

    public static Object getJsonFromLocation(Location l){

        if (l == null) return null;
        
        HashMap<String,Object> ret_val = new HashMap<>();
        Vector3d point = l.getPoint();
        if (point != null) {
            ret_val.put("point",getJsonFromVector3d(point));
        }

        Vector3d normal = l.getNormal();
        if (normal != null) {
            ret_val.put("normal",getJsonFromVector3d(normal));
        }

        return ret_val;
    }

    public static Object getJsonFromVector3dArray(Vector3d a[]){

        ArrayList list = new ArrayList();
        if(a != null){
            for(int i = 0; i < a.length; i++){
                list.add(getJsonFromVector3d(a[i]));
            }
        }
        return list;
    }

    /**
       convert parameter value into form suitable for Json serialization
     */
    public static Object getJsonValue(Object value, ParameterType type){
        
        //Object value = param.getValue();

        switch(type){
        default:
            throw new RuntimeException(fmt("getJsonValue(%s: of type:%s) not implemented\n", value, type));
        case DOUBLE:
        case FLOAT:
        case BYTE:
        case SHORT:
        case BOOLEAN:
        case INTEGER:
        case LONG:
        case ENUM:
        case STRING:
        case URI:
            return value;
        case LOCATION:            
            return getJsonFromLocation((Location)value);
        case VECTOR_3D:
            return getJsonFromVector3d((Vector3d)value);            
        case SNODE:
            return getJson((Parameterizable)value);
        case SNODE_LIST:
            return getJsonFromList((ArrayList)value, ParameterType.SNODE);
        case STRING_LIST:
            return getJsonFromList((ArrayList)value, ParameterType.STRING);
        case URI_LIST:
            return getJsonFromList((ArrayList)value, ParameterType.URI);
        case DOUBLE_LIST:
            return getJsonFromList((ArrayList)value, ParameterType.DOUBLE);
        case FLOAT_LIST:
            return getJsonFromList((ArrayList)value, ParameterType.FLOAT);
        case BYTE_LIST:
            return getJsonFromList((ArrayList)value, ParameterType.BYTE);
        case SHORT_LIST:
            return getJsonFromList((ArrayList)value, ParameterType.SHORT);
        case INTEGER_LIST:
            return getJsonFromList((ArrayList)value, ParameterType.INTEGER);
        case LONG_LIST:
            return getJsonFromList((ArrayList)value, ParameterType.LONG);
        case LOCATION_LIST:
            return getJsonFromList((ArrayList)value, ParameterType.LOCATION);
        case COLOR:
            return getJsonFromColor((Color)value);            
        case AXIS_ANGLE_4D:
            return getJsonFromAxisAngle4d((AxisAngle4d)value);            

        case OBJECT: 
            if(value != null) return value.toString();
            else return null;
        }
    }


    /**
       convert array of Parameter into Map suitable for Json serialization
     */
    public static Map getJson(Parameter params[]){

        LinkedHashMap map = new LinkedHashMap();

        for(int i = 0; i < params.length; i++){
            Parameter param = params[i]; 
            map.put(param.getName(),getJsonValue(param.getValue(),param.getType()));
        }
        return map;
    }

    /**
       convert Parameterizable into Map suitable for Json serialization
     */
    public static Map getJson(Parameterizable par){

        LinkedHashMap map = new LinkedHashMap();
        map.put(CLASS, par.getClass().getName());        
        map.put(PARAMS, getJson(par.getParams()));
        return map;
    } 


    public static Vector3d getVector3dFromJson(JsonElement value){
        JsonArray array = value.getAsJsonArray();
        if(array.size() >= 3) {
            double x = array.get(0).getAsDouble();
            double y = array.get(1).getAsDouble();
            double z = array.get(2).getAsDouble();
            return new Vector3d(x,y,z);
        } else {
            return null;
        }
    }

    public static Color getColorFromJson(JsonElement value){
        JsonArray array = value.getAsJsonArray();
        double x = array.get(0).getAsDouble();
        double y = array.get(1).getAsDouble();
        double z = array.get(2).getAsDouble();
        return new Color(x,y,z);
    }

    public static Location getLocationFromJson(JsonElement value){
        JsonObject obj = value.getAsJsonObject();
        JsonElement pointObj = obj.get("point");
        Vector3d point = null;
        if (pointObj != null) {
            point = getVector3dFromJson(pointObj);
        }
        JsonElement normalObj = obj.get("normal");
        Vector3d normal = null;
        if (normalObj != null) {
            normal = getVector3dFromJson(normalObj);
        }

        return new  Location(point,normal);

    }

    public static AxisAngle4d getAxisAngle4dFromJson(JsonElement value){
        JsonArray array = value.getAsJsonArray();
        double x = array.get(0).getAsDouble();
        double y = array.get(1).getAsDouble();
        double z = array.get(2).getAsDouble();
        double a = array.get(3).getAsDouble();
        return new AxisAngle4d(x,y,z,a);
    }

    public static ArrayList getStringListFromJson(JsonElement value){
        JsonArray array = value.getAsJsonArray();
        ArrayList str = new ArrayList(array.size());
        for(int i = 0; i < array.size(); i++){
            str.add(array.get(i).getAsString());
        } 
        return str;
    }

    
    public static Vector3d[] getVector3dArrayFromJson(JsonElement value, int expectedCount){

        JsonArray array = value.getAsJsonArray();        
        if(array.size() < expectedCount) 
            return null;

        Vector3d vect[] = new Vector3d[array.size()];        
        for(int i = 0; i < array.size(); i++){
            vect[i] = getVector3dFromJson(array.get(i));
        } 
        return vect;
    }

    public static Vector3d[] getVector3dArrayFromJson(JsonElement value){
        JsonArray array = value.getAsJsonArray();        
        Vector3d vect[] = new Vector3d[array.size()];
        for(int i = 0; i < array.size(); i++){
            vect[i] = getVector3dFromJson(array.get(i));
        } 
        return vect;
    }

    public static SNode getSNodeFromJson(JsonElement value){

        SNode snode = null;
        try {
            JsonObject obj = value.getAsJsonObject();
            String name = obj.get(CLASS).getAsString();
            snode = (SNode)(Class.forName(name).newInstance());
            Parameter params[] = ((Parameterizable)snode).getParams();
            JsonObject paramObj = obj.getAsJsonObject(PARAMS);
            getParamValuesFromJson(paramObj, params);            
        } catch(Exception e){
            e.printStackTrace();
        }
        return snode;
    }

    public static ArrayList getSNodeListFromJson(JsonElement value){
        JsonArray array = value.getAsJsonArray();
        ArrayList str = new ArrayList(array.size());
        for(int i = 0; i < array.size(); i++){
            str.add(getSNodeFromJson(array.get(i)));
        } 
        return str;
    }

    public static Map getUserDefinedFromJson(JsonObject value,UserDefinedParameter param) {
        HashMap<String,Parameter> ret_val = new HashMap<>();

        Map<String,Parameter> types = param.getProperties();

        for(Parameter p : types.values()) {
            JsonElement el = value.get(p.getName());

            if (el == null) continue;
            getParamValueFromJson(el,p);  // This feels weird to write the value into the types...
            ret_val.put(p.getName(),p);
        }

        return ret_val;
    }

    public static void getParamValueFromJson(JsonElement value, Parameter param){
        
        if(DEBUG) printf("parseJson(%s -> %s)\n", value, param);
        switch(param.getType()){
        default:
            throw new RuntimeException(fmt("getJsonValue(%s, type:%s) not implemented\n", value.getClass().getName(), param.getType()));
        case BOOLEAN:
            param.setValue(new Boolean(value.getAsJsonPrimitive().getAsBoolean()));  
            break;
        case DOUBLE:
            param.setValue(new Double(value.getAsJsonPrimitive().getAsDouble()));  
            break;  
        case FLOAT:
            param.setValue(new Float(value.getAsJsonPrimitive().getAsFloat()));            
            break;
        case BYTE:
            param.setValue(new Byte(value.getAsJsonPrimitive().getAsByte()));
            break;
        case SHORT:
            param.setValue(new Short(value.getAsJsonPrimitive().getAsShort()));
            break;
        case INTEGER:
            param.setValue(new Integer(value.getAsJsonPrimitive().getAsInt()));
            break;
        case LONG:
            param.setValue(new Long(value.getAsJsonPrimitive().getAsLong()));
            break;
        case STRING:
            param.setValue(value.getAsString());
            break;
        case STRING_LIST:
            param.setValue(getStringListFromJson(value));
            break;
        case LOCATION:
            param.setValue(getLocationFromJson(value));
            break;
        case ENUM:
            param.setValue(value.getAsString());
            break;
        case URI:
            param.setValue(value.getAsString());            
            break;
        case SNODE:
            param.setValue(getSNodeFromJson(value));
            break;
        case SNODE_LIST:
            param.setValue(getSNodeListFromJson(value));
            break;
        case VECTOR_3D:
            param.setValue(getVector3dFromJson(value));            
            break;
        case AXIS_ANGLE_4D:
            param.setValue(getAxisAngle4dFromJson(value));            
            break;
        case COLOR:
            param.setValue(getColorFromJson(value));            
            break;
        case USERDEFINED:
            param.setValue(getUserDefinedFromJson((JsonObject)value,(UserDefinedParameter)param));
        }

    }
    
    public static void getParamValuesFromJson(JsonObject obj, Parameter params[]){

        if(DEBUG) printf("parseJson(%s -> %s)\n", obj, params);
        
        for(int i = 0; i < params.length; i++){
            Parameter param = params[i];
            String name = param.getName();
            JsonElement value = obj.get(name);
            if(value != null)
                getParamValueFromJson(value, param);
        }
        
    }


    public static void getParamValuesFromJson(String json, Parameter params[]){

        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        //printf("elem: %s\n",obj.getClass().getName());
        getParamValuesFromJson(obj, params);
        
    }

    public static void getParamValuesFromJson(JsonObject obj, Map<String,Parameter> params){

        if(DEBUG) printf("parseJson(%s -> %s)\n", obj, params);

        for(Parameter param : params.values()){
            String name = param.getName();
            JsonElement value = obj.get(name);
            if(value != null)
                getParamValueFromJson(value, param);
        }

    }


    public static void getParamValuesFromJson(String json, Map<String,Parameter> params){

        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        //printf("elem: %s\n",obj.getClass().getName());
        getParamValuesFromJson(obj, params);

    }

    /**
       parses and set value of parameter from json string 
     */
    public static void getParamValueFromJson(String json, Parameter param){

        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        getParamValueFromJson(obj, param);
        
    }



    public static SNode getSNodeFromJson(String json){

        JsonParser parser = new JsonParser();        
        JsonObject obj = parser.parse(json).getAsJsonObject();
        return getSNodeFromJson(obj);        
    }

    /**
       return value of parameter as JSON string 
     */
    public static String getValueAsJsonString(Parameter param){

        Gson gson = new GsonBuilder().create();
        return gson.toJson(getJsonValue(param.getValue(), param.getType()));  

    }

}
