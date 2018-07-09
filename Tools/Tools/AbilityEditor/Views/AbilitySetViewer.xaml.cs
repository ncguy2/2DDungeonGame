using System;
using System.Windows.Controls;
using Shared.Data;

namespace AbilityEditor.Views {
    public partial class AbilitySetViewer : UserControl {
        public AbilitySetViewer() {
            InitializeComponent();
        }

        public Action<AbilityGroup> OnSelectionChanged;

        private void ItemList_SelectionChanged(object sender, SelectionChangedEventArgs e) {
            object item = ItemList.SelectedItem;
            OnSelectionChanged?.Invoke(item as AbilityGroup);
        }
    }
}
