import Member from "@/interfaces/Member";
import { ServerMembersFetcher } from "@/lib/fetchers";

export async function getMembers(): Promise<Member[]> {
  try {
    const response = await ServerMembersFetcher().get("/");
    return response.data;
  } catch (error) {
    console.error("Failed to fetch members:", error);
    return [];
  }
}
