// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.utils;

import org.apache.http.entity.ContentType;

public class MimeTypeHelper
{
    public static ContentType getContentTypeByMime(final String mime) {
        if (ContentType.APPLICATION_ATOM_XML.getMimeType().equals(mime)) {
            return ContentType.APPLICATION_ATOM_XML;
        }
        if (ContentType.APPLICATION_FORM_URLENCODED.getMimeType().equals(mime)) {
            return ContentType.APPLICATION_FORM_URLENCODED;
        }
        if (ContentType.APPLICATION_JSON.getMimeType().equals(mime)) {
            return ContentType.APPLICATION_JSON;
        }
        if (ContentType.APPLICATION_OCTET_STREAM.getMimeType().equals(mime)) {
            return ContentType.APPLICATION_OCTET_STREAM;
        }
        if (ContentType.APPLICATION_SVG_XML.getMimeType().equals(mime)) {
            return ContentType.APPLICATION_SVG_XML;
        }
        if (ContentType.APPLICATION_XHTML_XML.getMimeType().equals(mime)) {
            return ContentType.APPLICATION_XHTML_XML;
        }
        if (ContentType.APPLICATION_XML.getMimeType().equals(mime)) {
            return ContentType.APPLICATION_XML;
        }
        if (ContentType.DEFAULT_BINARY.getMimeType().equals(mime)) {
            return ContentType.DEFAULT_BINARY;
        }
        if (ContentType.DEFAULT_TEXT.getMimeType().equals(mime)) {
            return ContentType.DEFAULT_TEXT;
        }
        if (ContentType.MULTIPART_FORM_DATA.getMimeType().equals(mime)) {
            return ContentType.MULTIPART_FORM_DATA;
        }
        if (ContentType.TEXT_HTML.getMimeType().equals(mime)) {
            return ContentType.TEXT_HTML;
        }
        if (ContentType.TEXT_PLAIN.getMimeType().equals(mime)) {
            return ContentType.TEXT_PLAIN;
        }
        if (ContentType.TEXT_XML.getMimeType().equals(mime)) {
            return ContentType.TEXT_XML;
        }
        return ContentType.WILDCARD;
    }
}
