import CurrentMemberContext from "@/contexts/CurrentMemberContext";
import Member from "@/interfaces/Member";
import React, { useMemo, useState } from "react";

function CurrentMemberProvider({ children }: { children: React.ReactNode }) {
  const [currentMember, setCurrentMember] = useState<Member | null>(null);

  const value = useMemo(() => ({ currentMember, setCurrentMember }), [currentMember]);

  return <CurrentMemberContext value={value}>{children}</CurrentMemberContext>;
}

export default CurrentMemberProvider;
