using Shared.Data;

namespace AbilityEditor {
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow {

        public MainWindow() {
            InitializeComponent();

            AbilitySet set = new AbilitySet {Name = "Basics"};
            set.Groups.Add(new AbilityGroup {
                Name = "Cantrips",
                Abilities = {
                    new AbilityData {
                        Name = "Blink",
                        Icon = "",
                        Level = AbilityCost.Cantrip,
                        Script = "scripts/abilities/blink.js",
                        Cost = {},
                        Requirements = {
                            new ClassRequirement {
                                Classes = {
                                    Classes.Rogue
                                }
                            },
                            new LevelRequirement {
                                Level = 1
                            }
                        }
                    },
                    new AbilityData {
                        Name = "Heal",
                        Icon = "",
                        Level = AbilityCost.Cantrip,
                        Script = "scripts/abilities/heal.js",
                        Cost = {
                            AbilityComponent.Verbal,
                            AbilityComponent.Somatic
                        },
                        Requirements = {
                            new ClassRequirement {
                                Classes = {
                                    Classes.Bard,
                                    Classes.Cleric,
                                    Classes.Druid,
                                    Classes.Paladin,
                                    Classes.Wizard
                                }
                            },
                            new LevelRequirement {
                                Level = 1
                            }
                        }
                    }
                }
            });
            set.Groups.Add(new AbilityGroup {
                Name = "Sample 1",
                Abilities = {
                    new AbilityData {
                        Name = "Test 1",
                        Icon = "Icon 1",
                        Level = AbilityCost.Cantrip,
                    },
                    new AbilityData {
                        Name = "Test 2",
                        Icon = "Icon 2",
                        Level = AbilityCost.Cantrip,
                    },
                    new AbilityData {
                        Name = "Test 3",
                        Icon = "Icon 3",
                        Level = AbilityCost.Level_3,
                    }
                }
            });

            set.Groups.Add(new AbilityGroup {
                Name = "Sample 2",
                Abilities = {
                    new AbilityData {
                        Name = "Test 1",
                        Icon = "Icon 1",
                        Level = AbilityCost.Cantrip,
                    },
                    new AbilityData {
                        Name = "Test 2",
                        Icon = "Icon 2",
                        Level = AbilityCost.Cantrip,
                    },
                    new AbilityData {
                        Name = "Test 3",
                        Icon = "Icon 3",
                        Level = AbilityCost.Level_3,
                    }
                }
            });

            SetParent.SetViewer.ItemList.ItemsSource = set.Groups;
        }
    }
}