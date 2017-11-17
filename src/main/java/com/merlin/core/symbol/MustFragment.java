package com.merlin.core.symbol;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Created by ncm on 2017/5/27.
 */

@Documented
@Retention(CLASS)
@Target({PARAMETER, FIELD, ANNOTATION_TYPE})
public @interface MustFragment {
}
