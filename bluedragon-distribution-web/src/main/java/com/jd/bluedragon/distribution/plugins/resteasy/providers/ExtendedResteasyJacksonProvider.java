package com.jd.bluedragon.distribution.plugins.resteasy.providers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.map.util.JSONPObject;
import org.codehaus.jackson.type.JavaType;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJacksonProvider;

/**
 * 对ResteasyJacksonProvider的扩展， 用于解决使用Jackson序列化Json时null字符显示的问题。
 * 注意：Jackson版本不同，实现不同，当前扩展基于Jackson 1.7.1版本。 另一种实现方式， 通过对domain进行增加注解。
 *
 * @see http://wiki.fasterxml.com/JacksonAnnotationSerializeNulls
 * @author zhipeng.wang
 */
public class ExtendedResteasyJacksonProvider extends ResteasyJacksonProvider {

    @SuppressWarnings("deprecation")
    public void writeTo(Object value, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException {
        /*
         * 27-Feb-2009, tatu: Where can we find desired encoding? Within HTTP
         * headers?
         */
        ObjectMapper mapper = this.locateMapper(type, mediaType);
        JsonEncoding enc = this.findEncoding(mediaType, httpHeaders);
        JsonGenerator jg = mapper.getJsonFactory().createJsonGenerator(entityStream, enc);
        jg.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);

        // Want indentation?
        if (mapper.getSerializationConfig().isEnabled(SerializationConfig.Feature.INDENT_OUTPUT)) {
            jg.useDefaultPrettyPrinter();
        }
        // 04-Mar-2010, tatu: How about type we were given? (if any)
        JavaType rootType = null;

		if (genericType != null && value != null && genericType.getClass() != Class.class) {
			rootType = TypeFactory.type(genericType);
        }
        // [JACKSON-245] Allow automatic JSONP wrapping
        mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        if (this._jsonpFunctionName != null) {
            mapper.writeValue(jg, new JSONPObject(this._jsonpFunctionName, value, rootType));
        } else if (rootType != null) {
            mapper.typedWriter(rootType).writeValue(jg, value);
        } else {
            mapper.writeValue(jg, value);
        }
    }

    @SuppressWarnings("deprecation")
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream) throws IOException {
        ObjectMapper mapper = this.locateMapper(type, mediaType);
        mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        JsonParser jp = mapper.getJsonFactory().createJsonParser(entityStream);
        /*
         * Important: we are NOT to close the underlying stream after mapping,
         * so we need to instruct parser:
         */
        jp.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        return mapper.readValue(jp, this._convertType(genericType));
    }

}
