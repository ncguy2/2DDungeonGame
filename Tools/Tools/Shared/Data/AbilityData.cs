using System;

namespace Shared.Data {
    public class AbilityData {
        public string Name { get; set; }
        public string Icon { get; set; }
        public AbilityCost Level { get; set; }
        public string Script { get; set; }

        public Cost Cost { get; set; }
        public Requirements Requirements { get; set; }

        public AbilityData() {
            Cost = new Cost();
            Requirements = new Requirements();
        }

        public override string ToString() {
            return String.IsNullOrWhiteSpace(Name) ? base.ToString() : Name;
        }
    }
}