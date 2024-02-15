/*
 * This file is part of fabric-loom, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016-2023 FabricMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package lol.koblizek.amber.platform.fabric;

import org.objectweb.asm.*;

/**
 * @author FabricMC
 */
public final class FabricASM {
    public static final int ASM_VERSION = Opcodes.ASM9;

    /**
     * @see <a href="https://github.com/FabricMC/fabric-loom/blob/dev/1.5/src/main/java/net/fabricmc/loom/util/SyntheticParameterClassVisitor.java">Original Source Code</a>
     */
    public static class SnowmanClassVisitor extends ClassVisitor {
        public static class SnowmanMethodVisitor extends MethodVisitor {
            public SnowmanMethodVisitor(int api, MethodVisitor methodVisitor) {
                super(api, methodVisitor);
            }

            @Override
            public void visitParameter(final String name, final int access) {
                if (name != null && name.startsWith("\u2603")) {
                    super.visitParameter(null, access);
                } else {
                    super.visitParameter(name, access);
                }
            }

            @Override
            public void visitLocalVariable(
                    final String name,
                    final String descriptor,
                    final String signature,
                    final Label start,
                    final Label end,
                    final int index) {
                String newName = name;

                if (name != null && name.startsWith("\u2603")) {
                    newName = "lvt" + index;
                }

                super.visitLocalVariable(newName, descriptor, signature, start, end, index);
            }
        }

        public SnowmanClassVisitor(int api, ClassVisitor cv) {
            super(api, cv);
        }

        @Override
        public void visitSource(final String source, final String debug) {
            // Don't trust the obfuscation on this.
            super.visitSource(null, null);
        }

        @Override
        public MethodVisitor visitMethod(
                final int access,
                final String name,
                final String descriptor,
                final String signature,
                final String[] exceptions) {
            return new SnowmanMethodVisitor(api, super.visitMethod(access, name, descriptor, signature, exceptions));
        }
    }

    /**
     * @see <a href="https://github.com/FabricMC/fabric-loom/blob/dev/1.5/src/main/java/net/fabricmc/loom/util/SyntheticParameterClassVisitor.java">Original Source Code</a>
     */
    public static class SyntheticParameterClassVisitor extends ClassVisitor {
        private static class SyntheticMethodVisitor extends MethodVisitor {
            private final int offset;

            SyntheticMethodVisitor(int api, int offset, MethodVisitor methodVisitor) {
                super(api, methodVisitor);
                this.offset = offset;
            }

            @Override
            public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
                return super.visitParameterAnnotation(parameter - offset, descriptor, visible);
            }

            @Override
            public void visitAnnotableParameterCount(int parameterCount, boolean visible) {
                super.visitAnnotableParameterCount(parameterCount - offset, visible);
            }
        }

        private String className;
        private int synthetic;
        private String syntheticArgs;
        private boolean backoff = false;

        public SyntheticParameterClassVisitor(int api, ClassVisitor cv) {
            super(api, cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);

            this.className = name;

            // Enums will always have a string name and then the ordinal
            if ((access & Opcodes.ACC_ENUM) != 0) {
                synthetic = 2;
                syntheticArgs = "(Ljava/lang/String;I";
            }

            if (version >= 55) {
                // Backoff on java 11 or newer due to nest mates being used.
                backoff = true;
            }
        }

        @Override
        public void visitInnerClass(String name, String outerName, String innerName, int access) {
            super.visitInnerClass(name, outerName, innerName, access);

            // If we're a non-static, non-anonymous inner class then we can assume the first argument
            // is the parent class.
            // See https://docs.oracle.com/javase/specs/jls/se11/html/jls-8.html#jls-8.8.1
            if (synthetic == 0 && name.equals(this.className) && innerName != null && outerName != null && (access & Opcodes.ACC_STATIC) == 0) {
                this.synthetic = 1;
                this.syntheticArgs = "(L" + outerName + ";";
            }
        }

        @Override
        public MethodVisitor visitMethod(
                final int access,
                final String name,
                final String descriptor,
                final String signature,
                final String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

            return mv != null && synthetic != 0 && name.equals("<init>") && descriptor.startsWith(syntheticArgs) && !backoff
                    ? new SyntheticMethodVisitor(api, synthetic, mv)
                    : mv;
        }
    }

    public static final class Manifest {
        public static final String PATH = "META-INF/MANIFEST.MF";
        public static final String SPLIT_ENV = "Fabric-Loom-Split-Environment";
        public static final String SPLIT_ENV_NAME = "Fabric-Loom-Split-Environment-Name";
    }
}
