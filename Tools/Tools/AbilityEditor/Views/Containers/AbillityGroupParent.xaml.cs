using System.Windows.Controls;
using Shared.Data;

namespace AbilityEditor.Views {
    public partial class AbilityGroupParent : UserControl {
        public AbilityGroupParent() {
            InitializeComponent();
            GroupViewer.OnSelectionChanged += AbilityViewer.FocusOn;
        }

        public void FocusOn(AbilityGroup set) {
            GroupViewer.FocusOn(set);
            AbilityViewer.FocusOn(null);
        }

    }
}
