package netty.utils;

import io.netty.util.internal.ReflectionUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;

public class PlatformDependent {


    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PlatformDependent.class);


    /**
     * sun.misc.Unsafe
     */
    static  Unsafe UNSAFE;

    public static void main(String[] args) {
//        boolean windows0 = isWindows0();
//        System.out.println(windows0);
//
//        boolean osx0 = isOsx0();
//        System.out.println("osx0 = " + osx0);
//
//        boolean j9Jvm0 = isJ9Jvm0();
//        System.out.println(j9Jvm0);

        testUnsafe();
    }



    private static boolean isWindows0() {
        SecurityManager securityManager = System.getSecurityManager();

        String value = System.getProperty("os.name");
        boolean windows = SystemPropertyUtil.get("os.name", "").toLowerCase(Locale.US).contains("win");
        if (windows) {
            logger.debug("Platform: Windows");
        }
        return windows;
    }

    private static boolean isOsx0() {
        String osname = SystemPropertyUtil.get("os.name", "").toLowerCase(Locale.US);
        String s = osname.replaceAll("[^a-z0-9]+", "");
        String s1 = osname.replaceAll("[a-z]+", "");

        boolean osx = osname.startsWith("macosx") || osname.startsWith("osx");

        if (osx) {
            logger.debug("Platform: MacOS");
        }
        return osx;
    }

    private static boolean isJ9Jvm0() {
        // java hotspot(tm) 64-bit server vm
        String vmName = SystemPropertyUtil.get("java.vm.name", "").toLowerCase();
        return vmName.startsWith("ibm j9") || vmName.startsWith("eclipse openj9");
    }

    private static boolean isAndroid0() {
        String vmName = SystemPropertyUtil.get("java.vm.name");
        // java hotspot(tm) 64-bit server vm
        boolean isAndroid = "Dalvik".equals(vmName);
        if (isAndroid) {
            logger.debug("Platform: Android");
        }
        return isAndroid;
    }



    private static void testUnsafe() {



        // Buffer中address字段的反射
        Field addressField = null;

        ByteBuffer direct = ByteBuffer.allocateDirect(1);
        Method allocateArrayMethod = null;
        Throwable unsafeUnavailabilityCause = null;
        Unsafe unsafe;
        /**
         * 使用反射返回 UnSafe  也有可能返回Throwable
         */
        // attempt to access field Unsafe#theUnsafe
        final Object maybeUnsafe = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    final Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");

                    Throwable cause = ReflectionUtil.trySetAccessible(unsafeField, false);
                    if (cause != null) {
                        return cause;
                    }

                    return unsafeField.get(null);

                } catch (NoSuchFieldException e) {
                    return e;
                } catch (SecurityException e) {
                    return e;
                } catch (IllegalAccessException e) {
                    return e;
                } catch (NoClassDefFoundError e) {
                    return e;
                }
            }
        });

        if (maybeUnsafe instanceof Throwable) {
            unsafe = null;
            unsafeUnavailabilityCause = (Throwable) maybeUnsafe;
            logger.debug("sun.misc.Unsafe.theUnsafe: unavailable", (Throwable) maybeUnsafe);
        } else {
            unsafe = (Unsafe) maybeUnsafe;
            logger.debug("sun.misc.Unsafe.theUnsafe: available");
        }


        UNSAFE = unsafe;


        if (unsafe != null) {

            final Unsafe finalUnsafe = unsafe;

            // attempt to access field Buffer#address
            final Object maybeAddressField = AccessController.doPrivileged(new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    try {
                        // 获取 Buffer对象的address字段在Buffer对象内的便宜地址
                        final Field field = Buffer.class.getDeclaredField("address");

                        final long offset = finalUnsafe.objectFieldOffset(field);
                        // 获取地址中的值
                        final long address = finalUnsafe.getLong(direct, offset);

                        // if direct really is a direct buffer, address will be non-zero
                        if (address == 0) {
                            return null;
                        }
                        return field;
                    } catch (NoSuchFieldException e) {
                        return e;
                    } catch (SecurityException e) {
                        return e;
                    }
                }
            });

            // long java.nio.Buffer.address
            if (maybeAddressField instanceof Field) {
                addressField = (Field) maybeAddressField;
                logger.debug("java.nio.Buffer.address: available");
            } else {
                unsafeUnavailabilityCause = (Throwable) maybeAddressField;
                logger.debug("java.nio.Buffer.address: unavailable", (Throwable) maybeAddressField);

                // If we cannot access the address of a direct buffer, there's no point of using unsafe.
                // Let's just pretend unsafe is unavailable for overall simplicity.
                unsafe = null;
            }
        }





    }








    static Object getObject(Object object, long fieldOffset) {
        return UNSAFE.getObject(object, fieldOffset);
    }

    static int getInt(Object object, long fieldOffset) {
        return UNSAFE.getInt(object, fieldOffset);
    }

    private static long getLong(Object object, long fieldOffset) {
        return UNSAFE.getLong(object, fieldOffset);
    }

    static long objectFieldOffset(Field field) {
        return UNSAFE.objectFieldOffset(field);
    }

    static byte getByte(long address) {
        return UNSAFE.getByte(address);
    }

    static short getShort(long address) {
        return UNSAFE.getShort(address);
    }

    static int getInt(long address) {
        return UNSAFE.getInt(address);
    }

    static long getLong(long address) {
        return UNSAFE.getLong(address);
    }

//    /**
//     *
//     * @param data
//     * @param index
//     * @return
//     */
//    static byte getByte(byte[] data, int index) {
//        return UNSAFE.getByte(data, BYTE_ARRAY_BASE_OFFSET + index);
//    }
//
//    static short getShort(byte[] data, int index) {
//        return UNSAFE.getShort(data, BYTE_ARRAY_BASE_OFFSET + index);
//    }
//
//    static int getInt(byte[] data, int index) {
//        return UNSAFE.getInt(data, BYTE_ARRAY_BASE_OFFSET + index);
//    }
//
//    static long getLong(byte[] data, int index) {
//        return UNSAFE.getLong(data, BYTE_ARRAY_BASE_OFFSET + index);
//    }
//
//    /**
//     * put
//     */
//    static void putByte(long address, byte value) {
//        UNSAFE.putByte(address, value);
//    }
//
//    static void putShort(long address, short value) {
//        UNSAFE.putShort(address, value);
//    }
//
//    static void putInt(long address, int value) {
//        UNSAFE.putInt(address, value);
//    }
//
//    static void putLong(long address, long value) {
//        UNSAFE.putLong(address, value);
//    }
//
//    static void putByte(byte[] data, int index, byte value) {
//        UNSAFE.putByte(data, BYTE_ARRAY_BASE_OFFSET + index, value);
//    }
//
//    static void putShort(byte[] data, int index, short value) {
//        UNSAFE.putShort(data, BYTE_ARRAY_BASE_OFFSET + index, value);
//    }
//
//    static void putInt(byte[] data, int index, int value) {
//        UNSAFE.putInt(data, BYTE_ARRAY_BASE_OFFSET + index, value);
//    }
//
//    static void putLong(byte[] data, int index, long value) {
//        UNSAFE.putLong(data, BYTE_ARRAY_BASE_OFFSET + index, value);
//    }

}
