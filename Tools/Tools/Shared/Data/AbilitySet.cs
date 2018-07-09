using System;
using System.Collections.Generic;

namespace Shared.Data {
    public class AbilitySet {
        public String Name { get; set; }
        public List<AbilityGroup> Groups = new List<AbilityGroup>();
        public AbilityGroup this[int idx] => Groups[idx];

        public override string ToString() {
            return Name;
        }
    }
}