using System;
using System.Collections.Generic;
using System.Windows.Controls;
using Shared.Data;

namespace AbilityEditor.Views {
    public partial class AbilityGroupViewer : UserControl {

        private AbilityGroup focusedSet;
        public Action<AbilityData> OnSelectionChanged;

        public AbilityGroupViewer() {
            InitializeComponent();
        }

        private void ItemList_SelectionChanged(object sender, SelectionChangedEventArgs e) {

            if (focusedSet == null)
                return;

            OnSelectionChanged?.Invoke(ItemList.SelectedItem as AbilityData);
        }

        public void FocusOn(AbilityGroup group) {
            focusedSet = group;
            if (focusedSet == null)
                return;

            ItemList.ItemsSource = focusedSet.Abilities;
        }
    }
}
