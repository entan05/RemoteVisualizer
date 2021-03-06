﻿namespace RemoteVisualizerServer
{
    class Const
    {
        /// <summary>
        /// MainFormの基本タイトル
        /// </summary>
        public static readonly string MAIN_FORM_TITLE_BASE = "RemoteVisualizerServer";
    }

    class MouseEventCode
    {
        /// <summary>
        /// マウス左ボタン ダウン
        /// </summary>
        public static readonly int MOUSE_EVENT_LEFT_DOWN = 0x002;
        /// <summary>
        /// マウス左ボタン アップ
        /// </summary>
        public static readonly int MOUSE_EVENT_LEFT_UP = 0x004;

        /// <summary>
        /// 右ボタン ダウン
        /// </summary>
        public static readonly int MOUSE_EVENT_RIGHT_DOWN = 0x008;
        /// <summary>
        /// 右ボタン アップ
        /// </summary>
        public static readonly int MOUSE_EVENT_RIGHT_UP = 0x010;
    }
}
