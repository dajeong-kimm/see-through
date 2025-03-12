import React, { createContext, useMemo, useState } from "react";

interface Member {
  id: string;
  name: string;
  avatar: string;
}

const CurrentMemberContext = createContext<{
  currentMember: Member | null;
  // eslint-disable-next-line no-unused-vars
  setCurrentMember: (member: Member | null) => void;
}>({ currentMember: null, setCurrentMember: () => {} });

const CurrentMemberProvider = ({ children }: { children: React.ReactNode }) => {
  const [currentMember, setCurrentMember] = useState<Member | null>(null);

  const value = useMemo(() => ({ currentMember, setCurrentMember }), [currentMember]);

  return <CurrentMemberContext value={value}>{children}</CurrentMemberContext>;
};

export { CurrentMemberContext, CurrentMemberProvider };
