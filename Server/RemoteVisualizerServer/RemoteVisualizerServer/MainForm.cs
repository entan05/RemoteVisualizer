using System;
using System.Diagnostics;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
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
        /// 画面取得タイマー
        /// </summary>
        private System.Timers.Timer m_GettingImageTimer = null;

        private TcpClient m_TcpClient = null;

        /// <summary>
        /// コンストラクタ
        /// </summary>
        public MainForm()
        {
            InitializeComponent();

            GetHostAddress();

            UpdateFormTitle();

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
                                    UpdateLogBox(message);
                                }));
                            } while (m_TcpClient.Connected && !string.IsNullOrEmpty(message));
                        }
                        catch (IOException e)
                        { }
                    }
                }
            });
            task.Start();
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
                    m_TcpClient.Close();
                    m_TcpClient.Dispose();
                    m_TcpClient = null;
                }
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

                        Bitmap bitmap = new Bitmap(rectangle.Width, rectangle.Height);
                        Graphics graphics = Graphics.FromImage(bitmap);
                        graphics.CopyFromScreen(captureStartPoint, new Point(0, 0), rectangle.Size);
                        graphics.Dispose();

                        if (null != m_TcpClient && m_TcpClient.Connected)
                        {
                            MemoryStream memoryStream = new MemoryStream();
                            bitmap.Save(memoryStream, ImageFormat.Bmp);
                            byte[] imageBytes = memoryStream.ToArray();
                            memoryStream.Dispose();
                            string ImageBase64String = Convert.ToBase64String(imageBytes);

                            NetworkStream networkStream = m_TcpClient.GetStream();
                            byte[] sendBytes = Encoding.UTF8.GetBytes(ImageBase64String + "\n");
                            try
                            {
                                networkStream.Write(sendBytes, 0, sendBytes.Length);
                            }
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
    }
}
