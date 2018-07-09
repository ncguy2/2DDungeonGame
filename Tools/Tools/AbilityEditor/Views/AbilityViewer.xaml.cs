using System;
using System.Linq;
using System.Windows.Controls;
using Shared.Data;

namespace AbilityEditor.Views {
    public partial class AbilityViewer : UserControl {

        public AbilityViewer() {
            InitializeComponent();

            levelCostBox.ItemsSource = Enum.GetValues(typeof(AbilityCost)).Cast<AbilityCost>();
            componentCost.ItemsSource = Enum.GetValues(typeof(AbilityComponent)).Cast<AbilityComponent>();
        }

        public void FocusOn(AbilityData data) {
            Context.DataContext = data;
            // if (data != null) {
                // componentCost.SelectedItems.Clear();
                // data.Cost.ForEach(c => { componentCost.SelectedItems.Add(c); });
            // }
        }
    }
}
