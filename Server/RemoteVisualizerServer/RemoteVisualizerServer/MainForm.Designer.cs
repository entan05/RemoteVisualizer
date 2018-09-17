namespace RemoteVisualizerServer
{
    partial class MainForm
    {
        /// <summary>
        /// 必要なデザイナー変数です。
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// 使用中のリソースをすべてクリーンアップします。
        /// </summary>
        /// <param name="disposing">マネージ リソースを破棄する場合は true を指定し、その他の場合は false を指定します。</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows フォーム デザイナーで生成されたコード

        /// <summary>
        /// デザイナー サポートに必要なメソッドです。このメソッドの内容を
        /// コード エディターで変更しないでください。
        /// </summary>
        private void InitializeComponent()
        {
            this.ApplicationListView = new System.Windows.Forms.ListView();
            this.ApplicationListLabel = new System.Windows.Forms.Label();
            this.ApplicationListUpdateBtn = new System.Windows.Forms.Button();
            this.StateLogBox = new System.Windows.Forms.TextBox();
            this.FrameRateSlide = new System.Windows.Forms.TrackBar();
            this.PreviewBox = new System.Windows.Forms.PictureBox();
            this.ImageQualitySlide = new System.Windows.Forms.TrackBar();
            ((System.ComponentModel.ISupportInitialize)(this.FrameRateSlide)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.PreviewBox)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.ImageQualitySlide)).BeginInit();
            this.SuspendLayout();
            // 
            // ApplicationListView
            // 
            this.ApplicationListView.Location = new System.Drawing.Point(12, 37);
            this.ApplicationListView.MultiSelect = false;
            this.ApplicationListView.Name = "ApplicationListView";
            this.ApplicationListView.ShowGroups = false;
            this.ApplicationListView.Size = new System.Drawing.Size(158, 401);
            this.ApplicationListView.TabIndex = 0;
            this.ApplicationListView.UseCompatibleStateImageBehavior = false;
            this.ApplicationListView.View = System.Windows.Forms.View.List;
            this.ApplicationListView.SelectedIndexChanged += new System.EventHandler(this.ApplicationListView_SelectedIndexChanged);
            // 
            // ApplicationListLabel
            // 
            this.ApplicationListLabel.AutoSize = true;
            this.ApplicationListLabel.Location = new System.Drawing.Point(12, 13);
            this.ApplicationListLabel.Name = "ApplicationListLabel";
            this.ApplicationListLabel.Size = new System.Drawing.Size(68, 12);
            this.ApplicationListLabel.TabIndex = 1;
            this.ApplicationListLabel.Text = "Applications";
            // 
            // ApplicationListUpdateBtn
            // 
            this.ApplicationListUpdateBtn.Location = new System.Drawing.Point(95, 8);
            this.ApplicationListUpdateBtn.Name = "ApplicationListUpdateBtn";
            this.ApplicationListUpdateBtn.Size = new System.Drawing.Size(75, 23);
            this.ApplicationListUpdateBtn.TabIndex = 2;
            this.ApplicationListUpdateBtn.Text = "Update";
            this.ApplicationListUpdateBtn.UseVisualStyleBackColor = true;
            this.ApplicationListUpdateBtn.Click += new System.EventHandler(this.ApplicationListUpdateBtn_Click);
            // 
            // StateLogBox
            // 
            this.StateLogBox.HideSelection = false;
            this.StateLogBox.Location = new System.Drawing.Point(177, 329);
            this.StateLogBox.Multiline = true;
            this.StateLogBox.Name = "StateLogBox";
            this.StateLogBox.ScrollBars = System.Windows.Forms.ScrollBars.Vertical;
            this.StateLogBox.Size = new System.Drawing.Size(611, 109);
            this.StateLogBox.TabIndex = 3;
            // 
            // FrameRateSlide
            // 
            this.FrameRateSlide.Location = new System.Drawing.Point(587, 278);
            this.FrameRateSlide.Maximum = 60;
            this.FrameRateSlide.Minimum = 10;
            this.FrameRateSlide.Name = "FrameRateSlide";
            this.FrameRateSlide.Size = new System.Drawing.Size(201, 45);
            this.FrameRateSlide.TabIndex = 4;
            this.FrameRateSlide.Value = 30;
            // 
            // PreviewBox
            // 
            this.PreviewBox.Location = new System.Drawing.Point(468, 37);
            this.PreviewBox.Name = "PreviewBox";
            this.PreviewBox.Size = new System.Drawing.Size(320, 180);
            this.PreviewBox.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.PreviewBox.TabIndex = 5;
            this.PreviewBox.TabStop = false;
            // 
            // ImageQualitySlide
            // 
            this.ImageQualitySlide.Location = new System.Drawing.Point(177, 278);
            this.ImageQualitySlide.Maximum = 100;
            this.ImageQualitySlide.Minimum = 1;
            this.ImageQualitySlide.Name = "ImageQualitySlide";
            this.ImageQualitySlide.Size = new System.Drawing.Size(213, 45);
            this.ImageQualitySlide.SmallChange = 5;
            this.ImageQualitySlide.TabIndex = 6;
            this.ImageQualitySlide.Value = 20;
            this.ImageQualitySlide.ValueChanged += new System.EventHandler(this.ImageQualitySlide_ValueChanged);
            // 
            // MainForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(800, 450);
            this.Controls.Add(this.ImageQualitySlide);
            this.Controls.Add(this.PreviewBox);
            this.Controls.Add(this.FrameRateSlide);
            this.Controls.Add(this.StateLogBox);
            this.Controls.Add(this.ApplicationListUpdateBtn);
            this.Controls.Add(this.ApplicationListLabel);
            this.Controls.Add(this.ApplicationListView);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.Name = "MainForm";
            this.Text = "RemoteVisualizerServer";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.MainForm_FormClosing);
            ((System.ComponentModel.ISupportInitialize)(this.FrameRateSlide)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.PreviewBox)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.ImageQualitySlide)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.ListView ApplicationListView;
        private System.Windows.Forms.Label ApplicationListLabel;
        private System.Windows.Forms.Button ApplicationListUpdateBtn;
        private System.Windows.Forms.TextBox StateLogBox;
        private System.Windows.Forms.TrackBar FrameRateSlide;
        private System.Windows.Forms.PictureBox PreviewBox;
        private System.Windows.Forms.TrackBar ImageQualitySlide;
    }
}

