using System.Diagnostics;

namespace RemoteVisualizerServer
{
    public class ApplicationListItem
    {
        public Process process { get; }

        public ApplicationListItem(Process process)
        {
            this.process = process;
        }
    }
}
