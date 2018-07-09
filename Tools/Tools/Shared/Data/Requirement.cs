using System;
using System.Collections.Generic;

namespace Shared.Data {
    public abstract class Requirement {

    }

    public class ClassRequirement : Requirement {
        public List<Classes> Classes = new List<Classes>();
    }

    public class LevelRequirement : Requirement {
        public int Level;
    }

}