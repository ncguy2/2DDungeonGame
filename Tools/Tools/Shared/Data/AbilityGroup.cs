using System;
using System.Collections.Generic;

namespace Shared.Data {
    public class AbilityGroup {
        public String Name { get; set; }
        public List<AbilityData> Abilities = new List<AbilityData>();
        public int Size => Abilities.Count;

        public AbilityData this[int idx] => Abilities[idx];

        public override string ToString() {
            return Name;
        }
    }
}