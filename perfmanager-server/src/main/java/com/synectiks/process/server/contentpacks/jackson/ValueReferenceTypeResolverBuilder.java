/*
 * */
package com.synectiks.process.server.contentpacks.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.annotation.NoClass;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.jsontype.impl.AsPropertyTypeSerializer;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;

import java.util.Collection;

public class ValueReferenceTypeResolverBuilder extends StdTypeResolverBuilder {
    @Override
    public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
        if (_idType != JsonTypeInfo.Id.CUSTOM || _includeAs != JsonTypeInfo.As.WRAPPER_OBJECT) {
            return super.buildTypeDeserializer(config, baseType, subtypes);
        }

        final TypeIdResolver idRes = new ValueReferenceTypeIdResolver(baseType, config.getTypeFactory(), subtypes);
        final JavaType defaultImpl;
        if (_defaultImpl == null) {
            defaultImpl = null;
        } else {
            if ((_defaultImpl == Void.class) || (_defaultImpl == NoClass.class)) {
                defaultImpl = config.getTypeFactory().constructType(_defaultImpl);
            } else {
                defaultImpl = config.getTypeFactory().constructSpecializedType(baseType, _defaultImpl);
            }
        }

        return new AsValueReferenceTypeDeserializer(baseType, idRes, _typeProperty, _typeIdVisible, defaultImpl);
    }

    @Override
    public TypeSerializer buildTypeSerializer(SerializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
        if (_idType != JsonTypeInfo.Id.CUSTOM || _includeAs != JsonTypeInfo.As.WRAPPER_OBJECT) {
            return super.buildTypeSerializer(config, baseType, subtypes);
        }

        TypeIdResolver idRes = new ValueReferenceTypeIdResolver(baseType, config.getTypeFactory(), subtypes);
        return new AsPropertyTypeSerializer(idRes, null, this._typeProperty);
    }
}
