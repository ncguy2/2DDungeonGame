using System.Windows.Controls;

namespace AbilityEditor.Views {
    public partial class AbilitySetParent : UserControl {
        public AbilitySetParent() {
            InitializeComponent();

            SetViewer.OnSelectionChanged += GroupViewer.FocusOn;
        }
    }
}
