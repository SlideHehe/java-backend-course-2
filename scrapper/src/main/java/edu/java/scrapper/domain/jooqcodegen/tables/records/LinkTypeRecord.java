/*
 * This file is generated by jOOQ.
 */

package edu.java.scrapper.domain.jooqcodegen.tables.records;

import edu.java.scrapper.domain.jooqcodegen.tables.LinkType;
import jakarta.validation.constraints.Size;
import java.beans.ConstructorProperties;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;

/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.19.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({"all", "unchecked", "rawtypes", "this-escape"})
public class LinkTypeRecord extends UpdatableRecordImpl<LinkTypeRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.link_type.type</code>.
     */
    public void setType(@NotNull String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.link_type.type</code>.
     */
    @jakarta.validation.constraints.NotNull
    @Size(max = 63)
    @NotNull
    public String getType() {
        return (String) get(0);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    @NotNull
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached LinkTypeRecord
     */
    public LinkTypeRecord() {
        super(LinkType.LINK_TYPE);
    }

    /**
     * Create a detached, initialised LinkTypeRecord
     */
    @ConstructorProperties({"type"})
    public LinkTypeRecord(@NotNull String type) {
        super(LinkType.LINK_TYPE);

        setType(type);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised LinkTypeRecord
     */
    public LinkTypeRecord(edu.java.scrapper.domain.jooqcodegen.tables.pojos.LinkType value) {
        super(LinkType.LINK_TYPE);

        if (value != null) {
            setType(value.getType());
            resetChangedOnNotNull();
        }
    }
}