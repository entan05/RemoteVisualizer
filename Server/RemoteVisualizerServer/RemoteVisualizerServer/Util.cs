using System;
using System.Net;

namespace RemoteVisualizerServer
{
    class Util
    {
        public static IPAddress GetIpAddress()
        {
            IPAddress ipAddress = null;

            string hostName = Dns.GetHostName();
            IPAddress[] addresses = Dns.GetHostAddresses(hostName);

            string check = string.Empty;
            foreach (IPAddress address in addresses)
            {
                check = address.ToString();

                if (check.IndexOf('.') > 0 && !check.StartsWith("127.", StringComparison.CurrentCultureIgnoreCase))
                {
                    ipAddress = address;
                    break;
                }
            }

            return ipAddress;
        }

        /// <summary>
        /// マウスの左ボタンをダウンさせる
        /// </summary>
        public static void MouseLeftDown()
        {
            NativeCaller.mouse_event(MouseEventCode.MOUSE_EVENT_LEFT_DOWN, 0, 0, 0, 0);
        }

        /// <summary>
        /// マウスの左ボタンをアップさせる
        /// </summary>
        public static void MouseLeftUp()
        {
            NativeCaller.mouse_event(MouseEventCode.MOUSE_EVENT_LEFT_UP, 0, 0, 0, 0);
        }
    }
}
