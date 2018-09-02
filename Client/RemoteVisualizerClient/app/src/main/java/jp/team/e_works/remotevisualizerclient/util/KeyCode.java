package jp.team.e_works.remotevisualizerclient.util;

import android.view.KeyEvent;

public enum KeyCode {
    /** 不明なキー */
    UNKNOWN(KeyEvent.KEYCODE_UNKNOWN, -1),

    A(KeyEvent.KEYCODE_A, 65),
    B(KeyEvent.KEYCODE_B, 66),
    C(KeyEvent.KEYCODE_C, 67),
    D(KeyEvent.KEYCODE_D, 68),
    E(KeyEvent.KEYCODE_E, 69),
    F(KeyEvent.KEYCODE_F, 70),
    G(KeyEvent.KEYCODE_G, 71),
    H(KeyEvent.KEYCODE_H, 72),
    I(KeyEvent.KEYCODE_I, 73),
    J(KeyEvent.KEYCODE_J, 74),
    K(KeyEvent.KEYCODE_K, 75),
    L(KeyEvent.KEYCODE_L, 76),
    M(KeyEvent.KEYCODE_M, 77),
    N(KeyEvent.KEYCODE_N, 78),
    O(KeyEvent.KEYCODE_O, 79),
    P(KeyEvent.KEYCODE_P, 80),
    Q(KeyEvent.KEYCODE_Q, 81),
    R(KeyEvent.KEYCODE_R, 82),
    S(KeyEvent.KEYCODE_S, 83),
    T(KeyEvent.KEYCODE_T, 84),
    U(KeyEvent.KEYCODE_U, 85),
    V(KeyEvent.KEYCODE_V, 86),
    W(KeyEvent.KEYCODE_W, 87),
    X(KeyEvent.KEYCODE_X, 88),
    Y(KeyEvent.KEYCODE_Y, 89),
    Z(KeyEvent.KEYCODE_Z, 90),

    F1(KeyEvent.KEYCODE_F1, 112),
    F2(KeyEvent.KEYCODE_F2, 113),
    F3(KeyEvent.KEYCODE_F3, 114),
    F4(KeyEvent.KEYCODE_F4, 115),
    F5(KeyEvent.KEYCODE_F5, 116),
    F6(KeyEvent.KEYCODE_F6, 117),
    F7(KeyEvent.KEYCODE_F7, 118),
    F8(KeyEvent.KEYCODE_F8, 119),
    F9(KeyEvent.KEYCODE_F9, 120),
    F10(KeyEvent.KEYCODE_F10, 121),
    F11(KeyEvent.KEYCODE_F11, 122),
    F12(KeyEvent.KEYCODE_F12, 123);

    private int mAndroidKeyCode;
    private int mWindowsKeyCode;

    KeyCode(int androidKeyCode, int windowsKeyCode) {
        mAndroidKeyCode = androidKeyCode;
        mWindowsKeyCode = windowsKeyCode;
    }

    public int getAndroidKeyCode() {
        return mAndroidKeyCode;
    }

    public int getWindowsKeyCode() {
        return mWindowsKeyCode;
    }

    public static KeyCode getKeyCodeByAndroidKeyCode(int androidKeyCode) {
        KeyCode result = UNKNOWN;
        for (KeyCode key : KeyCode.values()) {
            if (key.getAndroidKeyCode() == androidKeyCode) {
                result = key;
                break;
            }
        }
        return result;
    }

    public static KeyCode getKetCodeByWindowsKeyCode(int windowsKeyCode) {
        KeyCode result = UNKNOWN;
        for (KeyCode key : KeyCode.values()) {
            if (key.getWindowsKeyCode() == windowsKeyCode) {
                result = key;
                break;
            }
        }
        return result;
    }
}
