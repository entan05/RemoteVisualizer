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
        private IPAddress m_IpAddress = null;
        private int m_Port = -1;

        private Process m_TargetProcess = null;

        private System.Timers.Timer m_GettingImageTimer = null;

        private TcpClient m_TcpClient = null;

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

        private void GetHostAddress()
        {
            m_IpAddress = Util.GetIpAddress();
            m_Port = 8888;
        }

        private void UpdateFormTitle()
        {
            UpdateLogBox("Update MainForm Title");
            if (null != m_IpAddress)
            {
                this.Text = Const.MAIN_FORM_TITLE_BASE + "_" + m_IpAddress + ":" + m_Port;
            }
            else
            {
                this.Text = Const.MAIN_FORM_TITLE_BASE;
            }
        }

        private void ReceiveStart()
        {
            Task task = new Task(() =>
            {
                if (null != m_IpAddress && m_Port > 0)
                {
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
                    }
                }
            });
            task.Start();
        }

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

        private void UpdateLogBox(string message)
        {
            StateLogBox.AppendText(message + "\r\n");
        }

        private void ApplicationListUpdateBtn_Click(object sender, EventArgs e)
        {
            ApplicationListView.Items.Clear();

            ListViewItem listViewItem;
            int listItemCount = 0;
            Process[] ps = Process.GetProcesses();
            foreach (Process p in ps)
            {
                if (!string.IsNullOrEmpty(p.MainWindowTitle) && null != p.MainWindowHandle)
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
                string selectedItem = ApplicationListView.SelectedItems[0].Text;
                UpdateLogBox("Selected : " + selectedItem);

                if (null != m_GettingImageTimer)
                {
                    m_GettingImageTimer.Stop();
                    m_GettingImageTimer.Dispose();
                    m_GettingImageTimer = null;
                }
                if (null != m_TargetProcess)
                {
                    NativeCaller.SetWindowPos(m_TargetProcess.MainWindowHandle,
                        (IntPtr)NativeCaller.SpecialWindowHandles.HWND_NOTOPMOST, 0, 0, 0, 0,
                        NativeCaller.SetWindowPosFlags.SWP_NOMOVE |
                        NativeCaller.SetWindowPosFlags.SWP_NOSIZE);
                    m_TargetProcess = null;
                }
                m_TargetProcess = ((ApplicationListItem)ApplicationListView.SelectedItems[0].Tag).process;
                NativeCaller.SetWindowPos(m_TargetProcess.MainWindowHandle,
                    (IntPtr)NativeCaller.SpecialWindowHandles.HWND_TOPMOST, 0, 0, 0, 0,
                    NativeCaller.SetWindowPosFlags.SWP_NOMOVE |
                    NativeCaller.SetWindowPosFlags.SWP_NOSIZE);

                // 仮で10fps
                m_GettingImageTimer = new System.Timers.Timer(1000 / 10);

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
                                networkStream.Write(sendBytes, 0, sendBytes.Length);
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
                        m_GettingImageTimer.Start();
                    }
                };

                m_GettingImageTimer.Start();
            }
        }
    }
}
