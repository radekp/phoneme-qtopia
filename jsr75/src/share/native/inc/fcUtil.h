/*
 * Copyright  1990-2009 Sun Microsystems, Inc. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt).
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa
 * Clara, CA 95054 or visit www.sun.com if you need additional
 * information or have any questions.
 */

#ifndef _FC_UTIL_H_
#define _FC_UTIL_H_


#include "javacall_defs.h"

/** @brief Maximum number of characters in string representation of suite ID. */
#define MAX_ID_LENGTH 12


#ifdef __cplusplus
extern "C" {
#endif

/**
 * Converts suite ID into its string representation.
 *
 * @param id suite ID to convert.
 * @param buf pre-allocated buffer the result will be written to.
 * @param len on input, pointer to the available buffer size;
 *            on output, pointer to store the resulting string length.
 * @return JAVACALL_OK on success, error code otherwise.
 */
javacall_result jsr75_get_suite_id_string(javacall_int32 id,
    /* OUT */ javacall_utf16 *buf, /* IN | OUT */ int *len);

#ifdef __cplusplus
}
#endif


#endif /* _FC_UTIL_H_ */
