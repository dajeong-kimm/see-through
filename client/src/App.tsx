import Header from "@/components/layout/Header";
import CurrentMemberContext from "@/contexts/CurrentMemberContext";
import Member from "@/interfaces/Member";
import MainPage from "@/pages/MainPage";
import { useMemo, useState } from "react";
function App() {
  // TODO: 추후에 서버에서 가져오는 데이터로 변경
  const initialMember: Member = {
    id: "1",
    name: "Gwon Hong",
    avatar: "https://github.com/shadcn.png",
  };

  const [currentMember, setCurrentMember] = useState<Member | null>(initialMember);
  const value = useMemo(() => ({ currentMember, setCurrentMember }), [currentMember]);

  return (
    <div className="max-w-md mx-auto bg-white min-h-screen">
      <CurrentMemberContext value={value}>
        <Header />
        <MainPage />
      </CurrentMemberContext>
    </div>
  );
}

export default App;
