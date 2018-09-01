using System.Diagnostics;

namespace RemoteVisualizerServer
{
    /// <summary>
    /// アプリリストのアイテム
    /// </summary>
    public class ApplicationListItem
    {
        public Process process { get; }

        public ApplicationListItem(Process process)
        {
            this.process = process;
        }
    }
}
