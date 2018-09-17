using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace RemoteVisualizerServer
{
    public partial class MainForm : Form
    {
        /// <summary>
        /// アプリ実行マシンのIPアドレス
        /// </summary>
        private IPAddress m_IpAddress = null;
        /// <summary>
        /// 使用ポート
        /// </summary>
        private int m_Port = -1;

        /// <summary>
        /// 画面取得対象プロセス
        /// </summary>
        private Process m_TargetProcess = null;
        /// <summary>
        /// 画面取得ウィンドウの位置
        /// </summary>
        private int m_TargetWindowLeft = -1;
        private int m_TargetWindowTop = -1;
        private int m_TargetWindowWidth = -1;
        private int m_TargetWindowHeight = -1;

        /// <summary>
        /// 画面取得タイマー
        /// </summary>
        private System.Timers.Timer m_GettingImageTimer = null;

        private long m_ImageQuality = 20L;

        private TcpClient m_TcpClient = null;

        private List<byte> m_PressingKeys;

        /// <summary>
        /// コンストラクタ
        /// </summary>
        public MainForm()
        {
            InitializeComponent();

            GetHostAddress();

            UpdateFormTitle();

            m_PressingKeys = new List<byte>(10);

            ReceiveStart();
        }

        private void MainForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (null != m_GettingImageTimer)
            {
                m_GettingImageTimer.Stop();
                m_GettingImageTimer.Dispose();
                m_GettingImageTimer = null;
            }
            if (null != m_TcpClient)
            {
                ReceiveStop();
            }
            SendAllKeyUp();
            if (null != m_TargetProcess)
            {
                NativeCaller.SetWindowPos(m_TargetProcess.MainWindowHandle,
                    (IntPtr)NativeCaller.SpecialWindowHandles.HWND_NOTOPMOST, 0, 0, 0, 0,
                    NativeCaller.SetWindowPosFlags.SWP_NOMOVE |
                    NativeCaller.SetWindowPosFlags.SWP_NOSIZE);
                m_TargetProcess = null;
            }
        }

        /// <summary>
        /// IPアドレス、ポートを取得する
        /// </summary>
        private void GetHostAddress()
        {
            m_IpAddress = Util.GetIpAddress();
            m_Port = 8888;
        }

        /// <summary>
        /// MainFormのタイトルを更新する
        /// </summary>
        private void UpdateFormTitle()
        {
            UpdateLogBox("Update MainForm Title");
            if (null != m_IpAddress)
            {
                Text = Const.MAIN_FORM_TITLE_BASE + "_" + m_IpAddress + ":" + m_Port;
            }
            else
            {
                Text = Const.MAIN_FORM_TITLE_BASE;
            }
        }

        /// <summary>
        /// 受信処理の開始
        /// </summary>
        private void ReceiveStart()
        {
            // 別スレッドで実行する
            Task task = new Task(() =>
            {
                // IPアドレス、ポートを取得していない場合は処理を行わない
                if (null != m_IpAddress && m_Port > 0)
                {
                    // 既に接続済みの場合は切断する
                    if (null != m_TcpClient)
                    {
                        ReceiveStop();
                    }

                    TcpListener tcpListener = new TcpListener(m_IpAddress, m_Port);
                    tcpListener.Start();

                    m_TcpClient = tcpListener.AcceptTcpClient();

                    if (m_TcpClient.Connected)
                    {
                        tcpListener.Stop();

                        NetworkStream networkStream = m_TcpClient.GetStream();
                        StreamReader streamReader = new StreamReader(networkStream, Encoding.UTF8);

                        string message = string.Empty;
                        try
                        {
                            do
                            {
                                message = streamReader.ReadLine();
                                if (null == message)
                                {
                                    break;
                                }
                                Invoke((Action)(() =>
                                {
                                    perseReceiveMessage(message);
                                }));
                            } while (m_TcpClient.Connected && !string.IsNullOrEmpty(message));
                        }
                        catch (IOException e)
                        { }
                    }
                    // 再接続待機
                    ReceiveStop();
                    ReceiveStart();
                }
            });
            task.Start();
        }

        private void perseReceiveMessage(string message)
        {
            string[] messageSplit = message.Split(',');

            // タッチイベント ダウン
            if ("00".Equals(messageSplit[0]))
            {
                int x = -1;
                int y = -1;
                if (int.TryParse(messageSplit[1], out x) && int.TryParse(messageSplit[2], out y))
                {
                    moveMouseByTouchPosition(x, y);
                }
                Util.MouseLeftDown();
                UpdateLogBox("touch down(" + x + ", " + y + ")");
            }
            // タッチイベント ムーブ
            else if ("01".Equals(messageSplit[0]))
            {
                int x = -1;
                int y = -1;
                if (int.TryParse(messageSplit[1], out x) && int.TryParse(messageSplit[2], out y))
                {
                    moveMouseByTouchPosition(x, y);
                    UpdateLogBox("touch move(" + x + ", " + y + ")");
                }
            }
            // タッチイベント アップ
            else if ("02".Equals(messageSplit[0]))
            {
                int x = -1;
                int y = -1;
                if (int.TryParse(messageSplit[1], out x) && int.TryParse(messageSplit[2], out y))
                {
                    moveMouseByTouchPosition(x, y);
                }
                Util.MouseLeftUp();
                UpdateLogBox("touch up(" + x + ", " + y + ")");
            }
            // 右クリックイベント
            else if ("03".Equals(messageSplit[0]))
            {
                if (m_TargetWindowWidth >= 0 && m_TargetWindowHeight >= 0)
                {
                    moveMouseByTouchPosition(m_TargetWindowWidth / 2, m_TargetWindowHeight / 2);
                    Util.MouseRightClick();
                }
            }
            // キーイベント ダウン
            else if ("10".Equals(messageSplit[0]))
            {
                int keyCode;
                if (int.TryParse(messageSplit[1], out keyCode))
                {
                    SendKeyDown((byte)keyCode);
                }
            }
            // キーイベント アップ
            else if ("11".Equals(messageSplit[0]))
            {
                int keyCode;
                if (int.TryParse(messageSplit[1], out keyCode))
                {
                    SendKeyUp((byte)keyCode);
                }
            }
            // キーイベント
            else if ("12".Equals(messageSplit[0]))
            {
                int keyCode;
                if (int.TryParse(messageSplit[1], out keyCode))
                {
                    SendKeyDown((byte)keyCode);
                    Thread.Sleep(50);
                    SendKeyUp((byte)keyCode);
                }
            }
        }

        private void moveMouseByTouchPosition(int x, int y)
        {
            if (m_TargetWindowLeft < 0 || m_TargetWindowTop < 0)
            {
                return;
            }
            NativeCaller.SetCursorPos(m_TargetWindowLeft + x, m_TargetWindowTop + y);
        }

        private void SendKeyDown(byte keyCode)
        {
            if (!m_PressingKeys.Contains(keyCode))
            {
                NativeCaller.keybd_event(keyCode, 0, 0, (UIntPtr)0);
                m_PressingKeys.Add(keyCode);
            }
        }

        private void SendKeyUp(byte keyCode)
        {
            int index = m_PressingKeys.IndexOf(keyCode);
            if (index >= 0)
            {
                NativeCaller.keybd_event(keyCode, 0, 2, (UIntPtr)0);
                m_PressingKeys.RemoveAt(index);
            }
        }

        private void SendAllKeyUp()
        {
            foreach (byte keyCode in m_PressingKeys)
            {
                NativeCaller.keybd_event(keyCode, 0, 2, (UIntPtr)0);
            }
            m_PressingKeys.Clear();
        }

        /// <summary>
        /// 切断処理
        /// </summary>
        private void ReceiveStop()
        {
            Task task = new Task(() =>
            {
                if (null != m_TcpClient)
                {
                    try
                    {
                        m_TcpClient.Close();
                        m_TcpClient.Dispose();
                        m_TcpClient = null;
                    }
                    catch (NullReferenceException e) { }
                }
                SendAllKeyUp();
            });
            task.Start();
        }

        /// <summary>
        /// 表示ログにメッセージを追加する
        /// </summary>
        /// <param name="message">追加するログメッセージ</param>
        private void UpdateLogBox(string message)
        {
            StateLogBox.AppendText(message + "\r\n");
        }

        private void ApplicationListUpdateBtn_Click(object sender, EventArgs e)
        {
            // プロセスリストのクリア
            ApplicationListView.Items.Clear();

            // 全プロセスを取得し、プロセスリストに追加する
            ListViewItem listViewItem;
            int listItemCount = 0;
            Process[] ps = Process.GetProcesses();
            foreach (Process p in ps)
            {
                if (!string.IsNullOrEmpty(p.MainWindowTitle) && !Text.Equals(p.MainWindowTitle) && null != p.MainWindowHandle)
                {
                    listViewItem = new ListViewItem(p.MainWindowTitle, listItemCount);
                    ApplicationListItem item = new ApplicationListItem(p);
                    listViewItem.Tag = item;
                    ApplicationListView.Items.Add(listViewItem);

                    listItemCount++;
                }
            }
        }

        private void ApplicationListView_SelectedIndexChanged(object sender, EventArgs eventArgs)
        {
            if (ApplicationListView.SelectedItems.Count > 0)
            {
                // 選択されたプロセスの取得
                string selectedItem = ApplicationListView.SelectedItems[0].Text;
                UpdateLogBox("Selected : " + selectedItem);

                // 画面取得タイマー実行中なら止める
                if (null != m_GettingImageTimer)
                {
                    m_GettingImageTimer.Stop();
                    m_GettingImageTimer.Dispose();
                    m_GettingImageTimer = null;
                }
                if (null != m_TargetProcess)
                {
                    ProcessTopMostRelease(m_TargetProcess);
                    m_TargetProcess = null;
                    m_TargetWindowLeft = -1;
                    m_TargetWindowTop = -1;
                    m_TargetWindowWidth = -1;
                    m_TargetWindowHeight = -1;
                }
                // 選択プロセスの取得
                m_TargetProcess = ((ApplicationListItem)ApplicationListView.SelectedItems[0].Tag).process;
                // 選択されたプロセスを最前面に変更
                ProcessTopMost(m_TargetProcess);
                // 最前面固定設定は解除する
                ProcessTopMostRelease(m_TargetProcess);

                // 画面取得タイマーを開始する
                StartGettingImageTimer();
            }
        }

        /// <summary>
        /// 画面取得タイマーを開始する
        /// </summary>
        private void StartGettingImageTimer()
        {
            // 仮で45fps
            m_GettingImageTimer = new System.Timers.Timer(1000 / 45);

            m_GettingImageTimer.Elapsed += (s, e) =>
            {
                try
                {
                    m_GettingImageTimer.Stop();

                    if (null != m_TargetProcess)
                    {
                        IntPtr handle = m_TargetProcess.MainWindowHandle;

                        NativeCaller.POINT screenPoint = new NativeCaller.POINT(0, 0);
                        NativeCaller.ClientToScreen(handle, out screenPoint);

                        NativeCaller.RECT clientRect = new NativeCaller.RECT();
                        NativeCaller.GetClientRect(handle, out clientRect);

                        Rectangle rectangle = new Rectangle(
                            clientRect.Left,
                            clientRect.Top,
                            clientRect.Right - clientRect.Left,
                            clientRect.Bottom - clientRect.Top);

                        Point captureStartPoint = new Point(
                            screenPoint.X + rectangle.X,
                            screenPoint.Y + rectangle.Y);

                        m_TargetWindowLeft = captureStartPoint.X;
                        m_TargetWindowTop = captureStartPoint.Y;
                        m_TargetWindowWidth = rectangle.Width;
                        m_TargetWindowHeight = rectangle.Height;

                        Bitmap bitmap = new Bitmap(rectangle.Width, rectangle.Height);
                        Graphics graphics = Graphics.FromImage(bitmap);
                        graphics.CopyFromScreen(captureStartPoint, new Point(0, 0), rectangle.Size);
                        graphics.Dispose();

                        if (null != m_TcpClient && m_TcpClient.Connected)
                        {
                            MemoryStream memoryStream = new MemoryStream();
                            EncoderParameters encoderParameters = new EncoderParameters(1);
                            EncoderParameter parameter = new EncoderParameter(System.Drawing.Imaging.Encoder.Quality, m_ImageQuality);
                            encoderParameters.Param[0] = parameter;
                            bitmap.Save(memoryStream, Util.GetEncoderInfo("image/jpeg"), encoderParameters);
                            byte[] imageBytes = memoryStream.ToArray();
                            memoryStream.Dispose();
                            string ImageBase64String = Convert.ToBase64String(imageBytes);

                            try
                            {
                                NetworkStream networkStream = m_TcpClient.GetStream();
                                byte[] sendBytes = Encoding.UTF8.GetBytes(ImageBase64String + "\n");
                                networkStream.BeginWrite(sendBytes, 0, sendBytes.Length, WriteResultCallback, networkStream);
                            }
                            catch (NullReferenceException ex) { }
                            catch (IOException ex) { }
                            catch (ObjectDisposedException ex) { }
                        }

                        if (null != PreviewBox.Image)
                        {
                            PreviewBox.Image.Dispose();
                        }
                        PreviewBox.Image = bitmap;
                    }
                }
                finally
                {
                    if (null != m_GettingImageTimer)
                    {
                        m_GettingImageTimer.Start();
                    }
                }
            };

            m_GettingImageTimer.Start();
        }

        private void WriteResultCallback(IAsyncResult asyncResult)
        {
            NetworkStream networkStream = (NetworkStream)asyncResult.AsyncState;
            try
            {
                networkStream.EndWrite(asyncResult);
            }
            catch (IOException e)
            {

            }
            catch (ObjectDisposedException e)
            {

            }
        }

        /// <summary>
        /// プロセスのウィンドウを最前面に変更する
        /// </summary>
        /// <param name="process">対象プロセス</param>
        private void ProcessTopMost(Process process)
        {
            if (null != process)
            {
                NativeCaller.SetWindowPos(process.MainWindowHandle,
                        (IntPtr)NativeCaller.SpecialWindowHandles.HWND_TOPMOST, 0, 0, 0, 0,
                        NativeCaller.SetWindowPosFlags.SWP_NOMOVE |
                        NativeCaller.SetWindowPosFlags.SWP_NOSIZE);
            }
        }

        /// <summary>
        /// プロセスの最前面固定設定を解除する
        /// </summary>
        /// <param name="process">対象プロセス</param>
        private void ProcessTopMostRelease(Process process)
        {
            if (null != process)
            {
                NativeCaller.SetWindowPos(process.MainWindowHandle,
                        (IntPtr)NativeCaller.SpecialWindowHandles.HWND_NOTOPMOST, 0, 0, 0, 0,
                        NativeCaller.SetWindowPosFlags.SWP_NOMOVE |
                        NativeCaller.SetWindowPosFlags.SWP_NOSIZE);
            }
        }

        private void ImageQualitySlide_ValueChanged(object sender, EventArgs e)
        {
            m_ImageQuality = ImageQualitySlide.Value;
            UpdateLogBox("ImageQuality : " + m_ImageQuality);
        }
    }
}
