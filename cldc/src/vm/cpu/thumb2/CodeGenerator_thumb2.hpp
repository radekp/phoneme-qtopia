/*
 *
 *
 * Copyright  1990-2006 Sun Microsystems, Inc. All Rights Reserved.
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

// This file is #include'd by src/vm/share/compiler/CodeGenerator.hpp inside
// the declaration of the CodeGenerator class.

private:
  void grow_stack(JVM_SINGLE_ARG_TRAPS);
  void call_through_gp(address& target JVM_TRAPS) {
    call_through_gp(target, true JVM_NO_CHECK_AT_BOTTOM);
  }
  void call_through_gp(address& target, bool add_lr JVM_TRAPS);

  void call_from_compiled_code(Register dst, int offset,
                               int parameters_size JVM_TRAPS) {
    call_from_compiled_code(dst, offset, parameters_size,
                            /*indirect=*/ false,
                            /*add_lr=*/ true
                            JVM_NO_CHECK_AT_BOTTOM);
  }

  void call_from_compiled_code(Register dst, int offset,
                               int parameters_size, bool indirect,
                               bool add_lr JVM_TRAPS);

  // BinaryAssembler.
  void arithmetic(Opcode opcode, Value& result, Value& op1, Value& op2);
  void larithmetic(Opcode opcode1, Opcode opcode2, Value& result, Value& op1,
                   Value& op2 JVM_TRAPS);
  void shift(Shift shifter, Value& result, Value& op1, Value& op2);

  void call_simple_c_runtime(Value& result, address runtime_func,
                          Value& op1, Value& op2) {
    vcall_simple_c_runtime(result, runtime_func, &op1, &op2, NULL);
  }
  void call_simple_c_runtime(Value& result, address runtime_func,
                          Value& op1) {
    vcall_simple_c_runtime(result, runtime_func, &op1, NULL);
  }

  void call_simple_c_runtime(Value& result, address runtime_func,
                          Value& op1, Value& op2, Value& op3) {
    vcall_simple_c_runtime(result, runtime_func, &op1, &op2, &op3, NULL);
  }

  void vcall_simple_c_runtime(Value& result, address runtime_func, ...);

  void idiv_rem(Value& result, Value& op1, Value& op2,
                bool isRemainder JVM_TRAPS);

  void lshift(Shift type, Value& result, Value& op1, Value& op2 JVM_TRAPS);

  void adjust_for_invoke(int parameters_size, BasicType return_type,
                         bool native = false);

  void setup_c_args(int ignored, ...);
  void vsetup_c_args(va_list ap);

  void shuffle_registers(Register* dstReg, Register* srcReg, int regCount);

  // Assign registers to result.  Try to reuse op if possible
  void assign_register(Value& result, Value& op);

  void restore_last_frame(Register return_address);

  void lookup_switch(Register index, jint table_index,
                     jint start, jint end, jint default_dest JVM_TRAPS);

#if ENABLE_ARM_VFP
  void ensure_in_float_register    (Value& value);
  void ensure_not_in_float_register(Value& value);

  void move_vfp_immediate(const Register dst,const jint src);
  void move_float_immediate(const Register dst, const jint src);
  void move_double_immediate(const Register dst, const jint src_lo, const jint src_hi);
#else
  void ensure_in_float_register    (Value& /*value*/) {}
  void ensure_not_in_float_register(Value& /*value*/) {}
#endif

public:
  void switch_to_arm_mode() {
    NOT_PRODUCT(comment("Switch from Thumb to ARM mode"));
    if ((code_size() & 0x3) != 0) {
      nop();
    }
    mov_hi(r12, pc);
    bx(r12);
    NOT_PRODUCT(comment("END : Switch from Thumb to ARM mode"));
  }

  void switch_to_thumb_mode() {
    NOT_PRODUCT(comment("Switch from ARM to Thumb mode"));
    // add r12, pc, 1 in ARM
    emit_int(0xE29 << 20 | pc << 16 |
         r12 << 12 | 0x1);
    // bx r12 in ARM
    emit_int(0xE << 28 | 0x012fff10 | r12);
    NOT_PRODUCT(comment("END : Switch from ARM to Thumb mode"));
  }


  void strb_arm(Register rs, Register rn, int offset) {
    int sign = 1;
    if (offset < 0) {
      sign = 0;
      offset = -offset;
    }

    int op2 = ((1 << 24) | sign << 23 | rn << 16 | offset);
    emit_int(0xE << 28 | 1 << 26 | rs << 12 | op2);
    return ;
  }

  void write_call_info(int parameters_size JVM_TRAPS);
  enum {
    // number of bytes between the start of the callinfo word and the start
    // of the first word of tagging
    extended_callinfo_tag_word_0_offset = -4,
    // number of bytes between the start of a word of tagging and the next
    // word of tagging.
    extended_callinfo_tag_word_n_offset = -4,
  };

  friend class CompilerLiteralAccessor;

  void increment_stack_pointer_by(int adjustment) {
    adjustment = JavaStackDirection * adjustment * BytesPerStackElement;
#if ENABLE_ARM_V7
   if (adjustment >= 0) {
     add(jsp, jsp, imm12(adjustment));
   } else {
     sub(jsp, jsp, imm12(-adjustment));
   }
#else
   add(jsp, jsp, adjustment);
#endif
  }

#if ENABLE_ARM_V7
  enum {
    check_stack_overflow_handler,
    frame_setup_handler,
    return_handler,
    return_error_handler,
    init_class_handler,
    invoke_heap_handler,
    invoke_rom_handler,
    invoke_virtual_heap_handler,
    invoke_virtual_rom_handler,
    invoke_interface_handler,
    do_not_use_it_2,
    method_prolog_handler_rom,
    do_not_use_it_5,
    do_not_use_it_6,
    method_prolog_handler_heap_ex_sens_update,
    do_not_use_it_7,
//    do_not_use_it_8,
    method_prolog_handler_rom_ex_sens_update,
    do_not_use_it_9,
    do_not_use_it_10,
    write_barrier_handler_r0,    // it doesn't require a parameter!
    write_barrier_handler_r1,    // it doesn't require a parameter!
    write_barrier_handler_r2,    // it doesn't require a parameter!
    write_barrier_handler_r3,    // it doesn't require a parameter!
    write_barrier_handler_r4,    // it doesn't require a parameter!
    write_barrier_handler_r5,    // it doesn't require a parameter!
    write_barrier_handler_r6,    // it doesn't require a parameter!
    write_barrier_handler_r7,    // it doesn't require a parameter!
    compiler_new_object_handler, // it doesn't require a parameter!
    do_not_use_it_11,
    do_not_use_it_12,
    comp_new_type_array_handler, // it doesn't require a parameter!
    do_not_use_it_13,
    do_not_use_it_14,
    comp_new_obj_array_handler,  // it doesn't require a parameter!
    do_not_use_it_15,
    do_not_use_it_16
  };
#endif

  void cmp_values(Value& op1, Value& op2,
                  BytecodeClosure::cond_op condition) {
    (void)condition;
    cmp_values(op1, op2);
  }

private:
  void cmp_values(Value& op1, Value& op2);
